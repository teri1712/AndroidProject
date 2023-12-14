package com.example.socialmediaapp.viewmodel.models.messenger;

public class ChatInfo {
   private Integer chatId;
   private String sender;

   public ChatInfo() {
   }

   public ChatInfo(Integer chatId, String sender) {
      this.chatId = chatId;
      this.sender = sender;
   }

   public Integer getChatId() {
      return chatId;
   }

   public void setChatId(Integer chatId) {
      this.chatId = chatId;
   }

   public String getSender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }
}
