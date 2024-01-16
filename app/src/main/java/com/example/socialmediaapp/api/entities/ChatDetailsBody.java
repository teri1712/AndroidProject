package com.example.socialmediaapp.api.entities;

import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

public class ChatDetailsBody {
   private UserBasicInfoBody userBasicInfoBody;
   private ChatInfo chatInfo;

   public UserBasicInfoBody getUserBasicInfoBody() {
      return userBasicInfoBody;
   }

   public void setUserBasicInfoBody(UserBasicInfoBody userBasicInfoBody) {
      this.userBasicInfoBody = userBasicInfoBody;
   }

   public ChatInfo getChatInfo() {
      return chatInfo;
   }

   public void setChatInfo(ChatInfo chatInfo) {
      this.chatInfo = chatInfo;
   }
}
