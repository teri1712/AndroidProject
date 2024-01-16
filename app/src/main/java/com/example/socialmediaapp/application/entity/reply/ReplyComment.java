package com.example.socialmediaapp.application.entity.reply;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.application.entity.comment.Comment;

@Entity(foreignKeys = {@ForeignKey(
        entity = UserBasicInfo.class,
        parentColumns = "autoId",
        childColumns = "userInfoId"),
        @ForeignKey(
                entity = Comment.class,
                parentColumns = "id",
                childColumns = "commentId")})
public class ReplyComment {
   @PrimaryKey
   @NonNull
   private String id;
   private Integer commentId;
   private Integer userInfoId;
   private String content;
   private Long time;
   private Integer likeCount;
   private boolean liked;
   private boolean mine;

   private Integer ord;

   private String imageId;
   private Integer imageWidth;
   private Integer imageHeight;

   public Integer getUserInfoId() {
      return userInfoId;
   }

   public void setUserInfoId(Integer userInfoId) {
      this.userInfoId = userInfoId;
   }

   public Integer getCommentId() {
      return commentId;
   }

   public void setCommentId(Integer commentId) {
      this.commentId = commentId;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public boolean isMine() {
      return mine;
   }

   public void setMine(boolean mine) {
      this.mine = mine;
   }

   public ReplyComment() {
   }

   public Integer getOrd() {
      return ord;
   }

   public void setOrd(Integer ord) {
      this.ord = ord;
   }

   public String getImageId() {
      return imageId;
   }

   public void setImageId(String imageId) {
      this.imageId = imageId;
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


   public void setLiked(boolean liked) {
      this.liked = liked;
   }

   public Integer getLikeCount() {
      return likeCount;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
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
