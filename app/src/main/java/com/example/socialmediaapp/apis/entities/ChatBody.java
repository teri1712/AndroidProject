package com.example.socialmediaapp.apis.entities;

public class ChatBody {
   private int chatId;
   private String sender;
   private int lastSeen;
   private MessageItemBody lastMessage;

   public int getChatId() {
      return chatId;
   }

   public MessageItemBody getLastMessage() {
      return lastMessage;
   }

   public void setLastMessage(MessageItemBody lastMessage) {
      this.lastMessage = lastMessage;
   }

   public void setChatId(int chatId) {
      this.chatId = chatId;
   }

   public String getSender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }

   public int getLastSeen() {
      return lastSeen;
   }

   public void setLastSeen(int lastSeen) {
      this.lastSeen = lastSeen;
   }
}
