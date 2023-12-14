package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.MessageSessionHandler;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageHomeViewModel extends ViewModel {
   private boolean endOfChatList;
   private MediatorLiveData<Boolean> loadState;
   private MessageSessionHandler messageSessionHandler;
   private List<ChatSessionModel> listChatSession;
   private MediatorLiveData<Update> chatListUpdate;

   public MessageHomeViewModel(MessageSessionHandler messageSessionHandler) {
      this.messageSessionHandler = messageSessionHandler;
      endOfChatList = false;
      loadState = new MediatorLiveData<>();
      listChatSession = new ArrayList<>();
      chatListUpdate = new MediatorLiveData<>();
      initBindingSessionChatList();
   }

   private void initBindingSessionChatList() {
      MessageSessionHandler.ChatSessionModelStore chatSessionModelStore = messageSessionHandler.getChatSessionModelStore();
      LiveData<Update> dataLayerChatSessionUpdate = chatSessionModelStore.getMsgSessionUpdate();
      chatListUpdate.addSource(dataLayerChatSessionUpdate, update -> {
         Update.Op op = update.op;
         assert op == Update.Op.ADD;

         HashMap<String, Object> data = update.data;
         int offset = (int) data.get("offset");

         if (offset != 0) {
            chatListUpdate.setValue(update);
            return;
         }
         ChatSessionModel item = (ChatSessionModel) data.get("item");
         for (int i = 0; i < listChatSession.size(); i++) {
            ChatSessionModel chatSessionModel = listChatSession.get(i);
            if (item == chatSessionModel) {
               listChatSession.remove(i);
               HashMap<String, Object> m = new HashMap<>();
               m.put("offset", i);
               chatListUpdate.setValue(new Update(Update.Op.REMOVE, m));
               break;
            }
         }
         listChatSession.add(0, item);
         HashMap<String, Object> m = new HashMap<>();
         m.put("offset", 0);
         m.put("length", 1);
         chatListUpdate.setValue(new Update(Update.Op.ADD, m));
      });
   }

   public MediatorLiveData<Boolean> getLoadState() {
      return loadState;
   }

   public void load() {
      if (endOfChatList || loadState.getValue()) {
         return;
      }
      loadState.setValue(true);
      int lastId = -1;
      if (!listChatSession.isEmpty()) {
         lastId = listChatSession.get(listChatSession.size() - 1).getChatInfo().getChatId();
      }
      LiveData<String> callBack = messageSessionHandler.loadMoreChatSession(lastId);
      loadState.addSource(callBack, s -> {
         if (s.equals("End")) {
            endOfChatList = true;
         }
         loadState.setValue(false);
      });
   }

   public List<ChatSessionModel> getListChatSession() {
      return listChatSession;
   }

   public LiveData<Update> getChatListUpdate() {
      return chatListUpdate;
   }
}
