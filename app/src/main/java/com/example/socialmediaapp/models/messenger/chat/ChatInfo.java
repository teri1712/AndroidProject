package com.example.socialmediaapp.models.messenger.chat;

public class ChatInfo {
  private String chatId;
  private String me;
  private String other;
  private String fullname;

  public ChatInfo() {
  }

  public ChatInfo(String chatId,
                  String me,
                  String other,
                  String fullname) {
    this.fullname = fullname;
    this.me = me;
    this.other = other;
    this.chatId = chatId;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }


  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public String getMe() {
    return me;
  }

  public void setMe(String me) {
    this.me = me;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }
}