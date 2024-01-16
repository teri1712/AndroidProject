package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.ChatThreadHandler;

public class MessageHomeViewModel extends ViewModel {
   private boolean endOfChatList;
   private MediatorLiveData<Boolean> loadState;
   private ChatThreadHandler messageThreadHandler;

   public MessageHomeViewModel(ChatThreadHandler messageThreadHandler) {
      this.messageThreadHandler = messageThreadHandler;
      endOfChatList = false;
      loadState = new MediatorLiveData<>();
      loadState.setValue(false);
   }

   public MediatorLiveData<Boolean> getLoadState() {
      return loadState;
   }

   public void load() {
      if (endOfChatList || loadState.getValue()) {
         return;
      }
      loadState.setValue(true);
      LiveData<String> callBack = messageThreadHandler.loadChat();
      loadState.addSource(callBack, s -> {
         if (s.equals("End")) {
            endOfChatList = true;
         }
         loadState.setValue(false);
      });
   }
}
