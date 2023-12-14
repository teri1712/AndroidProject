package com.example.socialmediaapp.viewmodel.messenger;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.MessageAccessHandler;
import com.example.socialmediaapp.layoutviews.items.MessageGroupLayoutManager;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.repo.MessageRepository;

public class MessageFragmentViewModel extends ViewModel {
   private ChatSessionModel chatSessionModel;
   private MessageRepository messageRepository;
   private MutableLiveData<String> sessionState;
   private MediatorLiveData<Boolean> loadMessageState;
   private boolean paused;

   public MessageFragmentViewModel(ChatSessionModel chatSessionModel) {
      super();
      this.chatSessionModel = chatSessionModel;
      MessageAccessHandler messageAccessHandler = chatSessionModel.getMessageAccessHandler();

      messageRepository = new MessageRepository(messageAccessHandler);
      sessionState = messageAccessHandler.getSessionState();
      loadMessageState = new MediatorLiveData<>();
      paused = false;
   }

   public ChatSessionModel getChatSessionModel() {
      return chatSessionModel;
   }

   public MutableLiveData<String> getSessionState() {
      return sessionState;
   }

   public MutableLiveData<Boolean> getLoadMessageState() {
      return loadMessageState;
   }

   public MessageRepository getMessageRepository() {
      return messageRepository;
   }

   public LiveData<String> uploadMessage(Bundle data) {
      return messageRepository.uploadNewItem(data);
   }

   public void load(int cnt) {
      if (loadMessageState.getValue() || paused) return;

      loadMessageState.setValue(true);
      LiveData<String> callBack = messageRepository.loadNewItems(cnt);
      loadMessageState.addSource(callBack, s -> {
         loadMessageState.removeSource(callBack);
         loadMessageState.setValue(false);
      });
   }

}
