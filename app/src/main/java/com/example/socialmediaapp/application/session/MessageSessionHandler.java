package com.example.socialmediaapp.application.session;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.ChatApi;
import com.example.socialmediaapp.apis.entities.ChatBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.dao.ChatDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.database.OrderedChat;
import com.example.socialmediaapp.application.entity.Chat;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatInfo;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.messenger.OnlineChatProvider;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

// the MessageSessionHandler used to manage the message chat box by order
// MessageSessionHandler intercept and delivery incoming messages to correspond chat box session,
// when chat box session completely process the incoming message, it will confirm with MessageSessionHandler by newMessageCompleteProcessed
// the confirmation will be handled and display to UI,
// the uploaded message of each chat box will also call newMessageCompleteProcessed
public class MessageSessionHandler extends SessionHandler {
   private final UserCredential credential;
   private final HashMap<Integer, Integer> chatSessionHandlers;
   private final MessageResolver messageResolver;
   private final ChatSessionModelStore chatSessionModelStore;
   private boolean isOnForeground;

   public MessageSessionHandler(UserCredential credential) {
      this.credential = credential;
      this.messageResolver = new MessageResolver();
      this.chatSessionHandlers = new HashMap<>();
      this.chatSessionModelStore = new ChatSessionModelStore();
   }

   @MainThread
   public boolean isOnForeground() {
      return isOnForeground;
   }

   @MainThread
   public void setOnForeground(boolean onForeground) {
      isOnForeground = onForeground;
   }

   public MessageResolver getMessageInterceptor() {
      return messageResolver;
   }

   public ChatSessionModelStore getChatSessionModelStore() {
      return chatSessionModelStore;
   }

   @Override
   protected void init() {
      super.init();
      // check if new user and pull the entire recent chat list.
      boolean isNewAccount = credential.isNewAccount();
      if (isNewAccount) {
         try {
            messageResolver.initAndPullChatList();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public void newMessageCompleteProcessed(Bundle msg) {
      post(() -> {
         AppDatabase db = ApplicationContainer.getInstance().database;
         ChatDao chatDao = db.getChatDao();
         SequenceDao sequenceDao = db.getSequenceDao();
         Integer chatId = msg.getInt("chat id");
         String sender = msg.getString("sender");

         db.runInTransaction(() -> {
            OrderedChat orderedChat = chatDao.findOrderedChatByChatId(chatId);
            if (orderedChat == null) {
               orderedChat = new OrderedChat();
               orderedChat.setChatId(chatId);
               orderedChat.setId((int) chatDao.insertOrderedChat(orderedChat));
            }
            orderedChat.setOrd(sequenceDao.getHeadValue());
            chatDao.updateOrderedChat(orderedChat);
         });
         ChatInfo chatInfo = new ChatInfo();
         chatInfo.setSender(sender);
         chatInfo.setChatId(chatId);

         final ChatSessionModel chatSessionModel = chatSessionModelStore.getChatSessionModel(getChatSession(chatInfo));
         Handler mainThread = new Handler(Looper.getMainLooper());
         mainThread.post(() -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put("item", chatSessionModel);
            data.put("offset", 0);
            chatSessionModelStore.msgSessionUpdate.postValue(new Update(Update.Op.ADD, data));
         });
      });
   }

   public LiveData<String> loadMoreChatSession(int lastId) {
      MutableLiveData<String> callBack = new MutableLiveData<>();
      post(() -> {
         AppDatabase db = ApplicationContainer.getInstance().database;
         ChatDao chatDao = db.getChatDao();

         List<Chat> chats = chatDao.loadNextChats(lastId);

         List<ChatSessionHandler> chatSessionHandlers = new ArrayList<>();
         for (Chat chat : chats) {
            Integer chatId = chat.getId();
            String sender = chat.getSender();

            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setSender(sender);
            chatInfo.setChatId(chatId);

            ChatSessionHandler chatSessionHandler = getChatSession(chatInfo);
            chatSessionHandlers.add(chatSessionHandler);
         }
         List<ChatSessionModel> chatSessionModels = new ArrayList<>();
         for (ChatSessionHandler chatSessionHandler : chatSessionHandlers) {
            chatSessionModels.add(chatSessionModelStore.create(chatSessionHandler));
         }
         if (chatSessionModels.isEmpty()) {
            callBack.postValue("End");
         } else {
            HashMap<String, Object> data = new HashMap<>();
            data.put("offset", -1);
            data.put("items", chatSessionModels);
            callBack.postValue("Success");
            chatSessionModelStore.msgSessionUpdate.postValue(new Update(Update.Op.ADD, data));
         }
      });
      return callBack;
   }

   private ChatSessionHandler getChatSession(ChatInfo chatInfo) {
      Integer chatId = chatInfo.getChatId();
      Integer sid = chatSessionHandlers.get(chatId);
      if (sid == null) {
         ChatSessionHandler chatSessionHandler = new ChatSessionHandler(chatInfo);
         sid = sessionRegistry.bind(chatSessionHandler);
         chatSessionHandlers.put(sid, chatId);
      }
      SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
      ChatSessionHandler chatSessionHandler = (ChatSessionHandler) sessionRepository.getSession(sid);
      return chatSessionHandler;
   }

   // Interceptor for FCM message notification
   public class MessageResolver {
      private String fbToken;

      private void initAndPullChatList() throws IOException {
         Object dummy = new Object();
         FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
               if (!task.isSuccessful()) {
                  assert false;
               }
               synchronized (dummy) {
                  fbToken = task.getResult();
                  dummy.notify();
               }
            }
         });
         synchronized (dummy) {
            if (fbToken == null) {
               try {
                  dummy.wait();
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }

         Call<List<ChatBody>> req = ApplicationContainer.getInstance().retrofit.create(ChatApi.class).initTokenAndPullChat(fbToken);

         AppDatabase db = ApplicationContainer.getInstance().database;
         SequenceDao sequenceDao = db.getSequenceDao();
         ChatDao chatDao = db.getChatDao();
         List<ChatBody> chatBodies = req.execute().body();
         List<OrderedChat> chats = new ArrayList<>();
         for (ChatBody chatBody : chatBodies) {
            Chat chat = ChatSessionHandler.pull(chatBody.getChatId());
            OrderedChat orderedChat = new OrderedChat();
            orderedChat.setChatId(chat.getId());
            orderedChat.setOrd(sequenceDao.getTailValue());
         }
         chatDao.insertAllOrderedChat(chats);
      }

      public void intercept(Bundle msg) {
         post(() -> {
            ChatInfo chatInfo = new ChatInfo(msg.getInt("chat id"), msg.getString("sender"));
            ChatSessionHandler chatSessionHandler = getChatSession(chatInfo);
            chatSessionHandler.onNewMessage(msg);
         });
      }
   }

   public class ChatSessionModelStore {
      final private MutableLiveData<Update> msgSessionUpdate;
      final private HashMap<Integer, ChatSessionModel> s;
      final private OnlineChatProvider chatSessionProvider = ApplicationContainer.getInstance().onlineSessionHandler.getOnlineChatProvider();

      @MainThread
      public LiveData<ChatSessionModel> findByChatId(Integer chatId) {
         MutableLiveData<ChatSessionModel> callback = new MutableLiveData<>();
         post(() -> callback.postValue(s.get(chatId)));
         return callback;
      }

      public ChatSessionModelStore() {
         s = new HashMap<>();
         msgSessionUpdate = new MutableLiveData<>();
      }

      private ChatSessionModel getChatSessionModel(ChatSessionHandler chatSessionHandler) {
         ChatSessionModel chatSessionModel = s.get(chatSessionHandler.chatInfo.getChatId());
         if (chatSessionModel == null) {
            chatSessionModel = create(chatSessionHandler);
         }
         return chatSessionModel;
      }

      private ChatSessionModel create(ChatSessionHandler chatSessionHandler) {

         ChatSessionModel chatSessionModel = new ChatSessionModel(chatSessionHandler);
         chatSessionModel.setMessageAccessHandler(chatSessionHandler.msgAccessHandler);

         chatSessionModelStore.add(chatSessionHandler.chatInfo.getChatId(), chatSessionModel);
         chatSessionProvider.apply(chatSessionModel);

         return chatSessionModel;
      }

      private void add(Integer chatId, ChatSessionModel chatSessionModel) {
         s.put(chatId, chatSessionModel);
      }

      public MutableLiveData<Update> getMsgSessionUpdate() {
         return msgSessionUpdate;
      }
   }
}
