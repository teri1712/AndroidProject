package com.example.socialmediaapp.application.entity.noification;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = NotificationItem.class
        , parentColumns = "id"
        , childColumns = "notiId"
        , onDelete = ForeignKey.CASCADE))
public class FriendRequestNotification {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private String notiId;
   private String userId;

   public Integer getId() {
      return id;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getNotiId() {
      return notiId;
   }

   public void setNotiId(String notiId) {
      this.notiId = notiId;
   }
}
