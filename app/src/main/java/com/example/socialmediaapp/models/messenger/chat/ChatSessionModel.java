package com.example.socialmediaapp.models.messenger.chat;

import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.session.ChatSessionHandler;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.google.firebase.database.DatabaseReference;

public class ChatSessionModel {
   private UserBasicInfoModel other;
   private ChatInfo chatInfo;
   private OnlineChat onlineChat;
   private ChatSessionHandler chatHandler;
   private TextingUpdater textingUpdater;

   public ChatSessionModel(ChatSessionHandler chatHandler) {
      this.chatHandler = chatHandler;
      this.chatInfo = chatHandler.getChatInfo();

      onlineChat = new OnlineChat();
      LiveData<ChatSessionHandler.ChatDataSync> dataSyncLiveData = chatHandler.getDataSyncLiveData();
      LiveData<Boolean> isActive = Transformations.switchMap(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, LiveData<Boolean>>() {
         @Override
         public LiveData<Boolean> apply(ChatSessionHandler.ChatDataSync input) {
            if (input.isActive() != null && !input.isActive()) {
               onlineChat.setOffTime(System.currentTimeMillis());
            }
            return input.isActive() == null ? null : new MutableLiveData<>(input.isActive());
         }
      });
      LiveData<Long> lastSeen = Transformations.map(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, Long>() {
         @Override
         public Long apply(ChatSessionHandler.ChatDataSync input) {
            return input.getLastSeen();
         }
      });
      LiveData<Long> meLastSeen = Transformations.map(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, Long>() {
         @Override
         public Long apply(ChatSessionHandler.ChatDataSync input) {
            return input.getMeLastSeen();
         }
      });
      LiveData<Bundle> lastMessage = Transformations.switchMap(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, LiveData<Bundle>>() {
         @Override
         public LiveData<Bundle> apply(ChatSessionHandler.ChatDataSync input) {
            Bundle msg = input.getLastMessage();
            return msg == null ? null : new MutableLiveData<>(msg);
         }
      });
      onlineChat.setIsActive(isActive);
      onlineChat.setLastMessage(lastMessage);
      onlineChat.setLastSeen(lastSeen);
      onlineChat.setMeLastSeen(meLastSeen);
      onlineChat.setIsTexting(null);
      other = dataSyncLiveData.getValue().getUserBasicInfo();
   }

   public void initOnlineTexting(DatabaseReference ref, LiveData<Boolean> isTexting) {
      onlineChat.setIsTexting(isTexting);
      textingUpdater = new TextingUpdater(ref);
   }

   public TextingUpdater getTextingUpdater() {
      return textingUpdater;
   }

   public ChatSessionHandler getChatHandler() {
      return chatHandler;
   }

   public UserBasicInfoModel getOther() {
      return other;
   }

   public OnlineChat getOnlineChat() {
      return onlineChat;
   }

   public ChatInfo getChatInfo() {
      return chatInfo;
   }

}
