package com.example.socialmediaapp.application.entity.reply;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.comment.Comment;


@Entity(foreignKeys =
        @ForeignKey(entity = Comment.class,
                parentColumns = "id",
                childColumns = "commentId",
                onDelete = ForeignKey.CASCADE))
public class ReplyCommentAccess {
   @PrimaryKey
   private long id;
   private String commentId;
   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getCommentId() {
      return commentId;
   }

   public void setCommentId(String commentId) {
      this.commentId = commentId;
   }
}
