package com.example.socialmediaapp.viewmodel.models.messenger;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class MessageItem {
   private Integer order;
   private long time;
   private String sender;
   private String type;
   private Integer chatId;

   public MessageItem() {
   }

   public String getType() {
      return type;
   }

   public Integer getChatId() {
      return chatId;
   }

   public void setChatId(Integer chatId) {
      this.chatId = chatId;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Integer getOrder() {
      return order;
   }

   public void setOrder(Integer order) {
      this.order = order;
   }

   public String getSender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }

   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }
}
