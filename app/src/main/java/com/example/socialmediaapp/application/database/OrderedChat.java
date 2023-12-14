package com.example.socialmediaapp.application.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.Chat;

@Entity(foreignKeys = @ForeignKey(
        entity = Chat.class,
        parentColumns = "id",
        childColumns = "chatId"
))
public class OrderedChat {
   @PrimaryKey
   private Integer id;
   private Integer ord;
   private Integer chatId;

   public Integer getOrd() {
      return ord;
   }

   public void setOrd(Integer ord) {
      this.ord = ord;
   }

   public Integer getChatId() {
      return chatId;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public void setChatId(Integer chatId) {
      this.chatId = chatId;
   }
}
