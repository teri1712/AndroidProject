package com.example.socialmediaapp.application.session;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.ChatApi;
import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.ChatBody;
import com.example.socialmediaapp.apis.entities.MessageItemBody;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.dao.ChatDao;
import com.example.socialmediaapp.application.dao.MessageDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.Chat;
import com.example.socialmediaapp.application.entity.MessageItem;
import com.example.socialmediaapp.application.entity.UserBasicInfo;
import com.example.socialmediaapp.application.session.helper.MessageAccessHelper;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatInfo;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Response;

public class ChatSessionHandler extends SessionHandler {
   public class ChatDataSync {
      private com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo;
      private Bundle lastMessage;
      private long lastSeen;
      private boolean isActive;

      public com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo getUserBasicInfo() {
         return userBasicInfo;
      }

      public Bundle getLastMessage() {
         return lastMessage;
      }

      public long getLastSeen() {
         return lastSeen;
      }

      public boolean isActive() {
         return isActive;
      }
   }

   private final Handler uiThread = new Handler(Looper.getMainLooper());
   private final MessageSessionHandler messageSessionHandler;
   protected final ChatInfo chatInfo;
   protected final MutableLiveData<ChatDataSync> dataSyncLiveData;
   protected MessageAccessHandler msgAccessHandler;
   protected MessageAccessHelper messageAccessHelper;
   private Queue<Bundle> msgQueue;

   public ChatSessionHandler(ChatInfo chatInfo) {
      this.chatInfo = chatInfo;
      this.dataSyncLiveData = new MutableLiveData<>(new ChatDataSync());
      this.messageSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler.getMessageSessionHandler();

      messageAccessHelper = new MessageAccessHelper(chatInfo.getChatId());
   }

   public MessageAccessHandler getMsgAccessHandler() {
      return msgAccessHandler;
   }

   public ChatInfo getChatInfo() {
      return chatInfo;
   }

   public MutableLiveData<ChatDataSync> getDataSyncLiveData() {
      return dataSyncLiveData;
   }

   private void initMessageDataAccess() {
      msgAccessHandler = new MessageAccessHandler(this);
      sessionRegistry.bind(msgAccessHandler);
   }

   protected static Chat pull(Integer chatId) throws IOException {
      MessageAccessHelper accessHelper = new MessageAccessHelper(chatId);
      AppDatabase db = ApplicationContainer.getInstance().database;
      ChatDao chatDao = db.getChatDao();
      Chat chat = new Chat();

      Call<ChatBody> chatBodyCall = ApplicationContainer.getInstance().retrofit.create(ChatApi.class).loadChat(chatId);

      ChatBody chatBody = chatBodyCall.execute().body();
      chat.setId(chatBody.getChatId());
      chat.setSender(chatBody.getSender());
      chat.setLastSeen(chatBody.getLastSeen());

      MessageItemBody lastMessage = chatBody.getLastMessage();

      Bundle msg = new Bundle();
      msg.putString("type", lastMessage.getType());
      msg.putInt("ord", lastMessage.getOrd());
      msg.putString("sender", lastMessage.getSender());
      msg.putLong("ime", lastMessage.getTime());
      msg.putInt("chat id", chatId);
      msg.putString("content", lastMessage.getContent());
      msg.putInt("media id", lastMessage.getMediaId());
      accessHelper.updateNewMessage(msg);

      chatDao.insert(chat);
      return chat;
   }

   @Override
   protected void init() {
      super.init();
      initMessageDataAccess();
      AppDatabase db = ApplicationContainer.getInstance().database;
      ChatDao chatDao = db.getChatDao();
      final boolean exists = chatDao.findChatById(chatInfo.getChatId()) != null;
      if (!exists) {
         try {
            pull(chatInfo.getChatId());
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         postToWorker(() -> {
            try {
               sync();
            } catch (IOException e) {
               e.printStackTrace();
            }
         });
      }
      postConstruct();
   }

   protected void postMessageProcess(Bundle newMsg) {
      uiThread.post(() -> {
         ChatDataSync dataSync = dataSyncLiveData.getValue();
         dataSync.lastMessage = newMsg;
         dataSyncLiveData.setValue(dataSync);
      });
      messageSessionHandler.newMessageCompleteProcessed(newMsg);
   }

   public void onNewMessage(Bundle msg) {
      if (msgQueue != null) {
         //if the chat session initialization hasn't completed, queue the message
         msgQueue.add(msg);
         return;
      }
      msgAccessHandler.updateNewMessage(msg);
      Bundle newMsg = new Bundle();
      String type = msg.getString("type");
      String sender = msg.getString("sender");
      if (type.equals("text")) {
         newMsg.putString("content", msg.getString("content"));
      } else if (type.equals("image")) {
         newMsg.putString("content", sender + " has sent an image");
      } else {
         newMsg.putString("content", sender + " has sent an icon");
      }
      newMsg.putString("sender", sender);
      newMsg.putInt("chat id", msg.getInt("chat id"));
      newMsg.putLong("time", msg.getLong("time"));

      postMessageProcess(newMsg);
   }

   public void onMessageSeen(long time) {
      AppDatabase db = ApplicationContainer.getInstance().database;
      ChatDao dao = db.getChatDao();
      db.runInTransaction(() -> {
         Chat chat = dao.findChatById(chatInfo.getChatId());
         chat.setLastSeen(time);
         dao.update(chat);
      });

      uiThread.post(() -> {
         ChatDataSync dataSync = dataSyncLiveData.getValue();
         dataSync.lastSeen = time;
         dataSyncLiveData.setValue(dataSync);
      });
   }

   public void onUserOnlineStateChanged(boolean isActive) {
      uiThread.post(() -> {
         ChatDataSync dataSync = dataSyncLiveData.getValue();
         dataSync.isActive = isActive;
         dataSyncLiveData.setValue(dataSync);
      });
   }

   // try to sync user information, maybe the user's name and avatar changed.
   private void sync() throws IOException {
      AppDatabase db = ApplicationContainer.getInstance().database;
      ChatDao chatDao = db.getChatDao();
      UserBasicInfoDao userBasicInfoDao = db.getUserBasicInfoDao();

      Chat chat = chatDao.findChatById(chatInfo.getChatId());
      if (chat == null) {
         chat = pull(chatInfo.getChatId());
      }
      Integer oldUserId = chat.getUserInfoId();

      Call<UserBasicInfoBody> req = ApplicationContainer.getInstance().retrofit.create(UserApi.class).loadUserBasicInfo(chatInfo.getSender());
      Response<UserBasicInfoBody> res = req.execute();
      MessageAccessHelper.LocalDataSupporter localDataSupporter = messageAccessHelper.getLocalDataSupporter();
      UserBasicInfoBody body = res.body();
      com.example.socialmediaapp.application.entity.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.application.entity.UserBasicInfo();
      userBasicInfo.setFullname(body.getFullname());
      userBasicInfo.setAlias(body.getAlias());
      userBasicInfo.setAvatarUri(localDataSupporter.downloadImage(body.getAvatarId()));

      int userId = (int) userBasicInfoDao.insert(userBasicInfo);
      chat.setUserInfoId(userId);
      chatDao.update(chat);

      if (oldUserId != null) {
         com.example.socialmediaapp.application.entity.UserBasicInfo oldUserInstance = userBasicInfoDao.findUserBasicInfo(oldUserId);
         File avatar = new File(oldUserInstance.getAvatarUri());
         avatar.delete();
         userBasicInfoDao.delete(oldUserInstance);
      }
      post(() -> loadInfoValue(userId));
   }

   private void loadInfoValue(Integer userId) {
      AppDatabase db = ApplicationContainer.getInstance().database;
      UserBasicInfoDao userBasicInfoDao = db.getUserBasicInfoDao();
      UserBasicInfo u = userBasicInfoDao.findUserBasicInfo(userId);
      com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
      userBasicInfo.setFullname(u.getFullname());
      userBasicInfo.setAlias(u.getAlias());
      userBasicInfo.setAvatar(BitmapFactory.decodeFile(u.getAvatarUri()));

      Handler mainThread = new Handler(Looper.getMainLooper());
      mainThread.post(() -> {
         ChatDataSync dataSync = dataSyncLiveData.getValue();
         dataSync.userBasicInfo = userBasicInfo;
         dataSyncLiveData.setValue(dataSync);
      });
   }

   private void initLiveData() {
      ChatDataSync dataSync = new ChatDataSync();

      AppDatabase db = ApplicationContainer.getInstance().database;
      ChatDao chatDao = db.getChatDao();
      MessageDao messageDao = db.getMessageDao();

      // load last message
      MessageItem lastMessage = chatDao.lastMessage(chatInfo.getChatId());
      Bundle msg = new Bundle();
      String type = lastMessage.getType();
      if (type.equals("text")) {
         msg.putString("content", messageDao.loadTextMessage(lastMessage.getId()).getContent());
      } else if (type.equals("image")) {
         msg.putString("content", lastMessage.getSender() + " has sent an image");
      } else {
         msg.putString("content", lastMessage.getSender() + " has sent an icon");
      }
      msg.putLong("time", lastMessage.getTime());
      dataSync.lastMessage = msg;

      //load last seen
      Chat chat = chatDao.findChatById(chatInfo.getChatId());
      dataSync.lastSeen = chat.getLastSeen();

      dataSyncLiveData.setValue(dataSync);
      //load user information
      loadInfoValue(chat.getUserInfoId());
   }

   private void postConstruct() {
      initLiveData();
      for (Bundle msg : msgQueue) {
         onNewMessage(msg);
      }
      msgQueue = null;
   }
}
