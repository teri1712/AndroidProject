package com.example.socialmediaapp.api.entities;

public class ReplyCommentDataSyncBody {
   private Integer countLike;
   private boolean like;

   public ReplyCommentDataSyncBody() {
   }

   public Integer getCountLike() {
      return countLike;
   }

   public boolean isLike() {
      return like;
   }

   public void setLike(boolean like) {
      this.like = like;
   }

   public void setCountLike(Integer countLike) {
      this.countLike = countLike;
   }

}
