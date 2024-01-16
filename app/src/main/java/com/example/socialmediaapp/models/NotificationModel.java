package com.example.socialmediaapp.models;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.lifecycle.LiveData;

public class NotificationModel {
   private Bundle action;
   private String avatarUri;
   private String content;
   private long time;

   public long getTime() {
      return time;
   }

   public String getAvatarUri() {
      return avatarUri;
   }

   public void setAvatarUri(String avatarUri) {
      this.avatarUri = avatarUri;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public Bundle getAction() {
      return action;
   }

   public void setAction(Bundle action) {
      this.action = action;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }
}
