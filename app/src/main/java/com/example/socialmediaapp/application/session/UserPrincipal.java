package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.api.entities.PrincipalBody;

public class UserPrincipal {
   private String username;
   private String userId;
   private boolean newAccount;
   private String fbToken;

   public String getFbToken() {
      return fbToken;
   }

   public void setFbToken(String fbToken) {
      this.fbToken = fbToken;
   }

   public UserPrincipal() {
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public boolean isNewAccount() {
      return newAccount;
   }

   public void setNewAccount(boolean newAccount) {
      this.newAccount = newAccount;
   }

   public String getUserId() {
      return userId;
   }
}
