package com.example.socialmediaapp.application.entity.comment;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

@Entity(foreignKeys = @ForeignKey(
        entity = UserBasicInfo.class,
        parentColumns = "autoId",
        childColumns = "userInfoId"
))
public class Comment {

  @PrimaryKey
  @NonNull
  private String id;
  private Integer userInfoId;
  private boolean mine;
  private String content;
  private Long time;
  private Integer likeCount;
  private boolean liked;
  private Integer ord;
  private Integer countReply;
  private String imageUri;
  private Integer imageWidth;
  private Integer imageHeight;

  /* for pending */

  private boolean committed;

  public Integer getCountReply() {
    return countReply;
  }

  public void setCountReply(Integer countReply) {
    this.countReply = countReply;
  }

  public Integer getUserInfoId() {
    return userInfoId;
  }

  public void setUserInfoId(Integer userInfoId) {
    this.userInfoId = userInfoId;
  }

  public Long getTime() {
    return time;
  }

  public boolean isCommitted() {
    return committed;
  }

  public void setCommitted(boolean committed) {
    this.committed = committed;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public boolean isMine() {
    return mine;
  }

  public Integer getOrd() {
    return ord;
  }

  public void setOrd(Integer ord) {
    this.ord = ord;
  }

  public void setMine(boolean mine) {
    this.mine = mine;
  }


  public Comment() {
  }

  public String getImageUri() {
    return imageUri;
  }

  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }

  public Integer getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(Integer imageWidth) {
    this.imageWidth = imageWidth;
  }

  public Integer getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(Integer imageHeight) {
    this.imageHeight = imageHeight;
  }

  public boolean isLiked() {
    return liked;
  }

  public String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = id;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  public Integer getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
