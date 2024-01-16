package com.example.socialmediaapp.application.entity.message;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

@Entity(foreignKeys = @ForeignKey(entity = UserBasicInfo.class,
        parentColumns = "autoId",
        childColumns = "otherId",
        onDelete = ForeignKey.CASCADE
))
public class Chat {
  @PrimaryKey
  @NonNull

  private String id;

  /* Chat info */
  private String other;
  private String me;
  private String fullname;

  private Integer otherId;
  private Long lastSeen;
  private Long meLastSeen;

  /* For ordering */
  private Long lastMsgTime;

  @NonNull
  public String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = id;
  }

  public Integer getOtherId() {
    return otherId;
  }

  public void setOtherId(Integer otherId) {
    this.otherId = otherId;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public String getMe() {
    return me;
  }

  public void setMe(String me) {
    this.me = me;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
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

  public Long getLastMsgTime() {
    return lastMsgTime;
  }

  public void setLastMsgTime(Long lastMsgTime) {
    this.lastMsgTime = lastMsgTime;
  }
}
