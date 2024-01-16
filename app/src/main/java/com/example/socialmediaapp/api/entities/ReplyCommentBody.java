package com.example.socialmediaapp.api.entities;

public class ReplyCommentBody {
   private String id;
   private String commentId;
   private UserBasicInfoBody author;
   private String content;
   private boolean liked;
   private Long time;
   private Integer countLike;
   private boolean mine;
   private int order;
   private ImageBody imageBody;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getCommentId() {
      return commentId;
   }

   public void setCommentId(String commentId) {
      this.commentId = commentId;
   }

   public UserBasicInfoBody getAuthor() {
      return author;
   }

   public void setAuthor(UserBasicInfoBody author) {
      this.author = author;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public boolean isLiked() {
      return liked;
   }

   public void setLiked(boolean liked) {
      this.liked = liked;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public Integer getCountLike() {
      return countLike;
   }

   public void setCountLike(Integer countLike) {
      this.countLike = countLike;
   }

   public boolean isMine() {
      return mine;
   }

   public void setMine(boolean mine) {
      this.mine = mine;
   }

   public int getOrder() {
      return order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public ImageBody getImageBody() {
      return imageBody;
   }

   public void setImageBody(ImageBody imageBody) {
      this.imageBody = imageBody;
   }
}
