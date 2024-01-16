package com.example.socialmediaapp.application.entity.noification;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class NotificationItem {
   @PrimaryKey
   @NonNull
   private String id;
   private String content;
   private boolean isRead;
   private long time;
   private String avatarUri;
   private String type;

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public long getTime() {
      return time;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public boolean isRead() {
      return isRead;
   }

   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }

   public void setRead(boolean read) {
      isRead = read;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getAvatarUri() {
      return avatarUri;
   }

   public void setAvatarUri(String avatarUri) {
      this.avatarUri = avatarUri;
   }
}
