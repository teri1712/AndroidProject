package com.example.socialmediaapp.application.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = UserBasicInfo.class, parentColumns = "autoId", childColumns = "userInfoId"))
public class Chat {
   @PrimaryKey
   private Integer id;
   private String sender;
   private Integer userInfoId;
   private long lastSeen;

   public String getSender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public long getLastSeen() {
      return lastSeen;
   }

   public void setLastSeen(long lastSeen) {
      this.lastSeen = lastSeen;
   }

   public Integer getUserInfoId() {
      return userInfoId;
   }

   public void setUserInfoId(Integer userInfoId) {
      this.userInfoId = userInfoId;
   }

}
