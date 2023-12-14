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
public class ImageMessageItem {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   @NonNull
   private Integer messageId;
   private String imageUri;

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

   public String getImageUri() {
      return imageUri;
   }

   public void setImageUri(String imageUri) {
      this.imageUri = imageUri;
   }
}
