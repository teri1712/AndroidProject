package com.example.socialmediaapp.api.entities;

public class NotificationPreset {
   private NotificationBody latestItem;
   private Integer countUnRead;

   public NotificationBody getLatestItem() {
      return latestItem;
   }

   public void setLatestItem(NotificationBody latestItem) {
      this.latestItem = latestItem;
   }

   public Integer getCountUnRead() {
      return countUnRead;
   }

   public void setCountUnRead(Integer countUnRead) {
      this.countUnRead = countUnRead;
   }
}
