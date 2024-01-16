package com.example.socialmediaapp.application.entity.message;


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
   private Integer width;
   private Integer height;

   public Integer getId() {
      return id;
   }

   @NonNull
   public Integer getMessageId() {
      return messageId;
   }

   public void setMessageId(@NonNull Integer messageId) {
      this.messageId = messageId;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getImageUri() {
      return imageUri;
   }

   public void setImageUri(String imageUri) {
      this.imageUri = imageUri;
   }

   public Integer getWidth() {
      return width;
   }

   public void setWidth(Integer width) {
      this.width = width;
   }

   public Integer getHeight() {
      return height;
   }

   public void setHeight(Integer height) {
      this.height = height;
   }
}
