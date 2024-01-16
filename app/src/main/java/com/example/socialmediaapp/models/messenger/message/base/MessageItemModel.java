package com.example.socialmediaapp.models.messenger.message.base;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class MessageItemModel {
  private Long time;
  private boolean isMine;
  private String type;
  private String chatId;
  private Integer msgId;
  private boolean unCommitted;

  public MessageItemModel() {
  }

  public String getType() {
    return type;
  }

  public String getChatId() {
    return chatId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isMine() {
    return isMine;
  }

  public void setMine(boolean mine) {
    isMine = mine;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(@NonNull Long time) {
    this.time = time;
  }

  public void setMsgId(Integer msgId) {
    this.msgId = msgId;
  }

  public Integer getMsgId() {
    return msgId;
  }
  public void setUnCommitted(boolean unCommitted) {
    this.unCommitted = unCommitted;
  }

  public boolean isUnCommitted() {
    return unCommitted;
  }
}
