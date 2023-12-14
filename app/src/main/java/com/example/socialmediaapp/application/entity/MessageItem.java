package com.example.socialmediaapp.application.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Chat.class, parentColumns = "id", childColumns = "chatBoxId", onDelete = ForeignKey.CASCADE))

public class MessageItem {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private Integer chatId;
   private String sender;
   private Long time;
   private String type;
   private Integer ord;

   public String getSender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }

   public Integer getOrd() {
      return ord;
   }

   public void setOrd(Integer ord) {
      this.ord = ord;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getChatId() {
      return chatId;
   }

   public void setChatId(Integer chatId) {
      this.chatId = chatId;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }
}
