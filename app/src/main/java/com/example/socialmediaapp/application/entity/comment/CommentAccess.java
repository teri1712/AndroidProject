package com.example.socialmediaapp.application.entity.comment;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.post.Post;


@Entity(foreignKeys =
@ForeignKey(entity = Post.class,
        parentColumns = "id",
        childColumns = "postId",
        onDelete = ForeignKey.CASCADE))
public class CommentAccess {
   @PrimaryKey
   private long id;

   private String postId;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }
   public String getPostId() {
      return postId;
   }
   public void setPostId(String postId) {
      this.postId = postId;
   }
}
