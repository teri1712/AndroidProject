package com.example.socialmediaapp.api.entities;

import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

public class ChatBody {
  private ChatInfo chatInfo;
  private UserBasicInfoBody other;
  private Long lastSeen;
  private Long meLastSeen;
  private MessageItemBody lastMessage;

  public ChatInfo getChatInfo() {
    return chatInfo;
  }

  public void setChatInfo(ChatInfo chatInfo) {
    this.chatInfo = chatInfo;
  }

  public UserBasicInfoBody getOther() {
    return other;
  }

  public void setOther(UserBasicInfoBody other) {
    this.other = other;
  }

  public Long getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(Long lastSeen) {
    this.lastSeen = lastSeen;
  }

  public Long getMeLastSeen() {
    return meLastSeen;
  }

  public void setMeLastSeen(Long meLastSeen) {
    this.meLastSeen = meLastSeen;
  }

  public MessageItemBody getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(MessageItemBody lastMessage) {
    this.lastMessage = lastMessage;
  }
}
