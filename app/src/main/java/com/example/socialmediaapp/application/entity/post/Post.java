package com.example.socialmediaapp.application.entity.post;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

@Entity(foreignKeys = @ForeignKey(entity = UserBasicInfo.class
        , parentColumns = "autoId"
        , childColumns = "userInfoId"
        , onDelete = ForeignKey.SET_NULL))
public class Post {
   @PrimaryKey
   @NonNull
   private String id;
   private Integer userInfoId;
   private String content;
   private String type;
   private Long time;
   private Integer likeCount, commentCount, shareCount;
   private boolean liked;
   public Integer getUserInfoId() {
      return userInfoId;
   }

   public void setUserInfoId(Integer userInfoId) {
      this.userInfoId = userInfoId;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public Post() {
   }

   public boolean isLiked() {
      return liked;
   }

   public Integer getShareCount() {
      return shareCount;
   }

   public void setShareCount(Integer shareCount) {
      this.shareCount = shareCount;
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

   public Integer getCommentCount() {
      return commentCount;
   }

   public void setCommentCount(Integer commentCount) {
      this.commentCount = commentCount;
   }

   public String getType() {
      return type;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

}
