package com.example.socialmediaapp.application.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = MessageItem.class,
        parentColumns = "id",
        childColumns = "messageId",
        onDelete = ForeignKey.CASCADE
))
public class IconMessageItem {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   @NonNull
   private Integer messageId;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getMessageId() {
      return messageId;
   }

   public void setMessageId(Integer messageId) {
      this.messageId = messageId;
   }
}
