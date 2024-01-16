package com.example.socialmediaapp.models.user;

public class FriendRequestModel {
   private Long time;
   private UserBasicInfoModel userModel;

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public UserBasicInfoModel getUserModel() {
      return userModel;
   }

   public void setUserModel(UserBasicInfoModel userModel) {
      this.userModel = userModel;
   }
}
