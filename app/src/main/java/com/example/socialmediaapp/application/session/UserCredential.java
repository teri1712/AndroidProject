package com.example.socialmediaapp.application.session;

public class UserCredential {
   private String username;
   private String alias;
   private boolean newAccount;

   public UserCredential() {

   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public boolean isNewAccount() {
      return newAccount;
   }

   public void setNewAccount(boolean newAccount) {
      this.newAccount = newAccount;
   }

   public String getAlias() {
      return alias;
   }
}
