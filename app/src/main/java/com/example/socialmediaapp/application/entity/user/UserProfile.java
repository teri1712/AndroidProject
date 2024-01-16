package com.example.socialmediaapp.application.entity.user;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

@Entity
public class UserProfile {
  @PrimaryKey
  @NonNull
  private String id;
  private String fullname;
  private String alias;
  private String gender;
  private String birthday;
  private String type;
  private String avatarPostId;
  private String backgroundPostId;
  private Integer avatarPostAccessId;
  private Integer backgroundPostAccessId;
  private String chatId;
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getId() {
    return id;
  }

  public String getAvatarPostId() {
    return avatarPostId;
  }

  public void setAvatarPostId(String avatarPostId) {
    this.avatarPostId = avatarPostId;
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

  public Integer getAvatarPostAccessId() {
    return avatarPostAccessId;
  }

  public void setAvatarPostAccessId(Integer avatarPostAccessId) {
    this.avatarPostAccessId = avatarPostAccessId;
  }

  public Integer getBackgroundPostAccessId() {
    return backgroundPostAccessId;
  }

  public void setBackgroundPostAccessId(Integer backgroundPostAccessId) {
    this.backgroundPostAccessId = backgroundPostAccessId;
  }

  public String getBackgroundPostId() {
    return backgroundPostId;
  }

  public void setBackgroundPostId(String backgroundPostId) {
    this.backgroundPostId = backgroundPostId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }


  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

}
