package com.example.socialmediaapp.viewmodel.models.messenger;

import android.os.Bundle;

import androidx.lifecycle.LiveData;

public class OnlineChat {
   private LiveData<Long> lastSeen;
   private LiveData<Boolean> isTexting;
   private LiveData<Boolean> isActive;
   private LiveData<Bundle> lastMessage;

   public OnlineChat() {
   }

   public LiveData<Long> getLastSeen() {
      return lastSeen;
   }

   public void setLastSeen(LiveData<Long> lastSeen) {
      this.lastSeen = lastSeen;
   }

   public void setIsTexting(LiveData<Boolean> isTexting) {
      this.isTexting = isTexting;
   }

   public LiveData<Boolean> getIsActive() {
      return isActive;
   }

   public void setIsActive(LiveData<Boolean> isActive) {
      this.isActive = isActive;
   }

   public LiveData<Bundle> getLastMessage() {
      return lastMessage;
   }

   public void setLastMessage(LiveData<Bundle> lastMessage) {
      this.lastMessage = lastMessage;
   }

   public LiveData<Boolean> getIsTexting() {
      return isTexting;
   }
}
