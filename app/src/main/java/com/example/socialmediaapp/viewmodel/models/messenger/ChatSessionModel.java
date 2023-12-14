package com.example.socialmediaapp.viewmodel.models.messenger;

import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.session.ChatSessionHandler;
import com.example.socialmediaapp.application.session.MessageAccessHandler;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class ChatSessionModel {
   private LiveData<UserBasicInfo> user;
   private ChatInfo chatInfo;
   private OnlineChat onlineChat;
   private MessageAccessHandler messageAccessHandler;
   private ChatSessionHandler chatSessionHandler;

   public ChatSessionModel(ChatSessionHandler chatSessionHandler) {
      this.chatSessionHandler = chatSessionHandler;
      this.chatInfo = chatSessionHandler.getChatInfo();
      messageAccessHandler = chatSessionHandler.getMsgAccessHandler();
      onlineChat = new OnlineChat();
      LiveData<ChatSessionHandler.ChatDataSync> dataSyncLiveData = chatSessionHandler.getDataSyncLiveData();
      LiveData<Boolean> isActive = Transformations.map(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, Boolean>() {
         @Override
         public Boolean apply(ChatSessionHandler.ChatDataSync input) {
            return input.isActive();
         }
      });
      LiveData<Long> lastSeen = Transformations.map(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, Long>() {
         @Override
         public Long apply(ChatSessionHandler.ChatDataSync input) {
            return input.getLastSeen();
         }
      });
      LiveData<Bundle> lastMessage = Transformations.map(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, Bundle>() {
         @Override
         public Bundle apply(ChatSessionHandler.ChatDataSync input) {
            return input.getLastMessage();
         }
      });
      onlineChat.setIsActive(isActive);
      onlineChat.setLastMessage(lastMessage);
      onlineChat.setLastSeen(lastSeen);
      onlineChat.setIsTexting(null);

      user = Transformations.map(dataSyncLiveData, new Function<ChatSessionHandler.ChatDataSync, UserBasicInfo>() {
         @Override
         public UserBasicInfo apply(ChatSessionHandler.ChatDataSync input) {
            return input.getUserBasicInfo();
         }
      });
   }

   public ChatSessionHandler getChatBoxSessionHandler() {
      return chatSessionHandler;
   }

   public LiveData<UserBasicInfo> getUser() {
      return user;
   }

   public void setUser(LiveData<UserBasicInfo> user) {
      this.user = user;
   }

   public OnlineChat getOnlineChat() {
      return onlineChat;
   }

   public ChatInfo getChatInfo() {
      return chatInfo;
   }

   public MessageAccessHandler getMessageAccessHandler() {
      return messageAccessHandler;
   }

   public void setMessageAccessHandler(MessageAccessHandler messageAccessHandler) {
      this.messageAccessHandler = messageAccessHandler;
   }
}
