package com.example.socialmediaapp.models.post.base;

import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class PostModel {
   private String id;
   private UserBasicInfoModel author;
   private String content;
   private String type;
   private Long time;
   private Integer likeCount, commentCount, shareCount;
   private boolean liked;

   public PostModel() {
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
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


   public UserBasicInfoModel getAuthor() {
      return author;
   }

   public String getType() {
      return type;
   }
   public void setType(String type) {
      this.type = type;
   }

   public void setAuthor(UserBasicInfoModel author) {
      this.author = author;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

}
