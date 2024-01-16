package com.example.socialmediaapp.api.entities;

public class CommentNotificationBody {
   private String commentId;
   private PostBody postBody;

   public CommentNotificationBody() {
   }

   public String getCommentId() {
      return commentId;
   }

   public PostBody getPostBody() {
      return postBody;
   }

   public void setPostBody(PostBody postBody) {
      this.postBody = postBody;
   }

   public void setCommentId(String commentId) {
      this.commentId = commentId;
   }

}
