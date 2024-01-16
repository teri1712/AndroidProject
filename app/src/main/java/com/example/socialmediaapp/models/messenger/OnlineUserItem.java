package com.example.socialmediaapp.models.messenger;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class OnlineUserItem {
   private ChatInfo chatInfo;
   private UserBasicInfoModel userBasicInfoModel;
   private MutableLiveData<Boolean> isOnline;

   public ChatInfo getChatInfo() {
      return chatInfo;
   }

   public MutableLiveData<Boolean> getIsOnline() {
      return isOnline;
   }

   public void setIsOnline(MutableLiveData<Boolean> isOnline) {
      this.isOnline = isOnline;
   }

   public void setChatInfo(ChatInfo chatInfo) {
      this.chatInfo = chatInfo;
   }

   public UserBasicInfoModel getUserBasicInfo() {
      return userBasicInfoModel;
   }

   public void setUserBasicInfo(UserBasicInfoModel userBasicInfoModel) {
      this.userBasicInfoModel = userBasicInfoModel;
   }
}
