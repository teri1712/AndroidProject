package com.example.socialmediaapp.api.entities;

import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

public class UserProfileBody {
   private ChatInfo chatInfo;
   private String fullname;
   private String id;
   private String gender;
   private String alias;
   private String birthday;
   private PostBody avatarPost;
   private PostBody backgroundPost;
   private String type;

   public UserProfileBody() {
   }

   public ChatInfo getChatInfo() {
      return chatInfo;
   }

   public void setChatInfo(ChatInfo chatInfo) {
      this.chatInfo = chatInfo;
   }

   public String getType() {
      return type;
   }

   public String getAlias() {
      return alias;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public PostBody getAvatarPost() {
      return avatarPost;
   }

   public void setAvatarPost(PostBody avatarPost) {
      this.avatarPost = avatarPost;
   }

   public PostBody getBackgroundPost() {
      return backgroundPost;
   }

   public void setBackgroundPost(PostBody backgroundPost) {
      this.backgroundPost = backgroundPost;
   }

   public String getFullname() {
      return fullname;
   }

   public void setFullname(String fullname) {
      this.fullname = fullname;
   }

   public String getBirthday() {
      return birthday;
   }

   public void setBirthday(String birthday) {
      this.birthday = birthday;
   }

   public String getGender() {
      return gender;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

}
