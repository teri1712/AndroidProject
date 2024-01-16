package com.example.socialmediaapp.application.session;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.ChatApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.ChatBody;
import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.message.ChatDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.message.Chat;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.ImageUtils;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ChatSessionHandler extends SessionHandler {
  public class ChatDataSync {
    private UserBasicInfoModel userModel;
    private Bundle lastMessage;
    private Long lastSeen;
    private Long meLastSeen;
    private Boolean isActive;

    public ChatDataSync() {
    }

    public UserBasicInfoModel getUserBasicInfo() {
      return userModel;
    }

    public Bundle getLastMessage() {
      return lastMessage;
    }

    public Long getLastSeen() {
      return lastSeen;
    }

    public Long getMeLastSeen() {
      return meLastSeen;
    }

    public Boolean isActive() {
      return isActive;
    }
  }

  private final DecadeDatabase db;
  private final UserBasicInfoDao userDao;
  private final ChatDao dao;
  protected final ChatInfo chatInfo;
  protected final MutableLiveData<ChatDataSync> dataSyncLiveData;
  private final Handler uiThread = new Handler(Looper.getMainLooper());
  private final ChatThreadHandler msgThreadHandler;
  protected MessageAccessHandler accessHandler;
  protected MessageAccessHelper accessHelper;

  public ChatSessionHandler(ChatInfo chatInfo,
                            ChatThreadHandler msgThreadHandler) {
    this.db = DecadeDatabase.getInstance();
    this.dao = db.getChatDao();
    this.userDao = db.getUserBasicInfoDao();
    this.chatInfo = chatInfo;
    this.dataSyncLiveData = new MutableLiveData<>();
    this.msgThreadHandler = msgThreadHandler;
    accessHelper = new MessageAccessHelper(chatInfo.getChatId());
    init();
  }

  public ChatInfo getChatInfo() {
    return chatInfo;
  }

  public MessageAccessHandler getAccessHandler() {
    return accessHandler;
  }

  public MutableLiveData<ChatDataSync> getDataSyncLiveData() {
    return dataSyncLiveData;
  }

  private Chat pull() throws IOException {
    String chatId = chatInfo.getChatId();

    MessageAccessHelper accessHelper = new MessageAccessHelper(chatId);

    Chat chat = new Chat();
    Call<ChatBody> call = HttpCallSupporter
            .create(ChatApi.class)
            .loadChat(chatId);
    Response<ChatBody> res = call.execute();
    HttpCallSupporter.debug(res);
    ChatBody chatBody = res.body();

    UserBasicInfo otherInfo = DtoConverter.convertToUserBasicInfo(chatBody.getOther());
    int userId = (int) userDao.insert(otherInfo);
    chat.setId(chatInfo.getChatId());
    chat.setOther(chatInfo.getOther());
    chat.setMe(chatInfo.getMe());
    chat.setOtherId(userId);
    chat.setFullname(chatInfo.getFullname());
    chat.setLastSeen(chatBody.getLastSeen());
    chat.setMeLastSeen(chatBody.getMeLastSeen());
    dao.insert(chat);
    MessageItemBody lastMessage = chatBody.getLastMessage();
    if (lastMessage != null) {
      List<MessageItemBody> item = new ArrayList<>();
      item.add(lastMessage);
      Map<String, Object> map = new ArrayMap<>();
      map.put("items", lastMessage);
      accessHelper.update(map);
    }
    return chat;
  }

  @Override
  protected void sync() {
    // will implement later
    super.sync();
  }

//  private void syncUserValues() throws IOException {
//    UserBasicInfoDao userBasicInfoDao = application.database.getUserBasicInfoDao();
//
//    Chat chat = chatDao.findChatById(chatInfo.getChatId());
//    Integer oldUserId = chat.getUserInfoId();
//
//    Response<UserBasicInfoBody> res = DecadeApplication
//            .getInstance()
//            .retrofit
//            .create(UserApi.class)
//            .loadUserBasicInfo(chatInfo.getOther()).execute();
//    UserBasicInfoBody body = res.body();
//    UserBasicInfo userBasicInfo = new UserBasicInfo();
//    userBasicInfo.setFullname(body.getFullname());
//    userBasicInfo.setId(body.getId());
//    userBasicInfo.setAvatarId(body.getAvatarId());
//
//    int userId = (int) userBasicInfoDao.insert(userBasicInfo);
//    chat.setUserInfoId(userId);
//    chatDao.update(chat);
//
//    if (oldUserId != null) {
//      UserBasicInfo oldUserInstance = userBasicInfoDao.findUserBasicInfo(oldUserId);
//      if (oldUserInstance.getAvatarId() != null) {
//        File avatar = new File(oldUserInstance.getAvatarId());
//        avatar.delete();
//      }
//      userBasicInfoDao.delete(oldUserInstance);
//    }
//    post(() -> {
//      Handler mainThread = new Handler(Looper.getMainLooper());
//      UserBasicInfoModel u = loadInfoValue(userId);
//      mainThread.post(() -> {
//        ChatDataSync dataSync = dataSyncLiveData.getValue();
//        dataSync.userBasicInfoModel = u;
//        dataSyncLiveData.setValue(dataSync);
//      });
//    });
//  }

  @Override
  protected void init() {
    super.init();
    final boolean exists = dao.findChatById(chatInfo.getChatId()) != null;
    if (!exists) {
      try {
        pull();
      } catch (IOException e) {
        e.printStackTrace();
        assert false;
      }
    } else {
//         postToWorker(() -> {
//            try {
//               syncUserValues();
//            } catch (IOException e) {
//               e.printStackTrace();
//            }
//         });
      sync();
    }
    postConstruct();
  }

  protected void onNewMessage(Bundle msg) {
    msg.putString("chat id", chatInfo.getChatId());
    uiThread.post(() -> {
      ChatDataSync dataSync = dataSyncLiveData.getValue();
      dataSync.lastMessage = msg;
      dataSyncLiveData.setValue(dataSync);
    });
    boolean isMine = msg.getString("sender").equals("You");
    if (!isMine) {
      UserBasicInfo u = dao.loadPartnerInfo(chatInfo.getChatId());
      msg.putString("avatar uri", ImageUtils.imagePrefUrl + u.getAvatarId());
    }
    msgThreadHandler.onMessageProcessed(msg);
  }

  public void onMessageSeen(long time) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
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
    });
    TaskRequest request = builder
            .setAlias("Chat" + chatInfo.getChatId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  public void onMeSeenMessage(long time) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      db.runInTransaction(() -> {
        Chat chat = dao.findChatById(chatInfo.getChatId());
        chat.setMeLastSeen(time);
        dao.update(chat);
      });
      try {
        HttpCallSupporter.create(ChatApi.class)
                .seenMessage(chatInfo.getChatId(), time)
                .execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
      FirebaseDatabase.getInstance().getReference()
              .child("chat")
              .child(chatInfo.getChatId())
              .child(chatInfo.getMe())
              .child("seen")
              .setValue(time);
      uiThread.post(() -> {
        ChatDataSync dataSync = dataSyncLiveData.getValue();
        dataSync.meLastSeen = time;
        dataSyncLiveData.setValue(dataSync);
      });
    });
    TaskRequest request = builder
            .setAlias("Chat" + chatInfo.getChatId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  public void onUserOnlineStateChanged(boolean isActive) {
    ChatDataSync dataSync = dataSyncLiveData.getValue();
    if (!isActive && dataSync.isActive == null) return;

    dataSync.isActive = isActive;
    dataSyncLiveData.setValue(dataSync);
  }

  private void initMessageDataAccess() {
    accessHandler = new MessageAccessHandler(this);
  }

  private void initLiveData() {
    ChatDataSync dataSync = new ChatDataSync();

    Chat chat = dao.findChatById(chatInfo.getChatId());
    MessageItem lastMsg = dao.lastMessage(chatInfo.getChatId());

    if (lastMsg != null) {
      Bundle msg = new Bundle();
      String type = lastMsg.getType();
      String name = lastMsg.getMine() ? "You" : chatInfo.getFullname();
      msg.putString("sender", name);
      if (type.equals("text")) {
        msg.putString("view content", db.getMessageDao().loadTextMessage(lastMsg.getId()).getContent());
      } else if (type.equals("image")) {
        msg.putString("view content", name + " has sent an image");
      } else {
        msg.putString("view content", name + " has sent an icon");
      }
      msg.putLong("time", lastMsg.getTime());
      dataSync.lastMessage = msg;
    }
    dataSync.lastSeen = chat.getLastSeen();
    if (dataSync.lastSeen == null) dataSync.lastSeen = 0L;
    dataSync.meLastSeen = chat.getMeLastSeen();
    if (dataSync.meLastSeen == null) dataSync.meLastSeen = 0L;

    UserBasicInfo u = userDao.findUser(chat.getOtherId());
    dataSync.userModel = ModelConvertor.convertToUserModel(u);

    dataSyncLiveData.postValue(dataSync);
  }

  private void loadPreferences() {
  }

  private void postConstruct() {
    loadPreferences();
    initMessageDataAccess();
    initLiveData();
  }
}
