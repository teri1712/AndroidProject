package com.example.socialmediaapp.application.entity.message;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.message.Chat;
import com.example.socialmediaapp.application.entity.pend.PendTask;

@Entity(foreignKeys = {
        @ForeignKey(entity = Chat.class,
                parentColumns = "id",
                childColumns = "chatId",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(entity = PendTask.class,
                parentColumns = "id",
                childColumns = "pendId",
                onDelete = ForeignKey.CASCADE
        )
})
public class MessageItem {
  @PrimaryKey(autoGenerate = true)
  private Integer id;
  private String chatId;
  private Boolean isMine;
  private String messageId;
  private Long time;
  private String type;
  private Integer ord;
  private String pendId;

  public Boolean getMine() {
    return isMine;
  }

  public void setMine(@NonNull Boolean mine) {
    isMine = mine;
  }

  public Integer getOrd() {
    return ord;
  }

  public void setOrd(Integer ord) {
    this.ord = ord;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getChatId() {
    return chatId;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public String getPendId() {
    return pendId;
  }

  public void setPendId(String pendId) {
    this.pendId = pendId;
  }
}
