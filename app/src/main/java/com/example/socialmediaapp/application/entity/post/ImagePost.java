package com.example.socialmediaapp.application.entity.post;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.post.Post;

@Entity(foreignKeys = @ForeignKey(
        entity = Post.class,
        parentColumns = "id",
        childColumns = "postId",
        onDelete = ForeignKey.CASCADE
))
public class ImagePost {
   @PrimaryKey(autoGenerate = true)
   private Integer autoId;
   private String postId;
   private Integer width;
   private Integer height;
   private String mediaId;

   public ImagePost() {
   }

   public Integer getAutoId() {
      return autoId;
   }

   public void setAutoId(Integer autoId) {
      this.autoId = autoId;
   }

   public String getPostId() {
      return postId;
   }

   public void setPostId(String postId) {
      this.postId = postId;
   }

   public Integer getWidth() {
      return width;
   }

   public void setWidth(Integer width) {
      this.width = width;
   }

   public Integer getHeight() {
      return height;
   }

   public void setHeight(Integer height) {
      this.height = height;
   }

   public String getMediaId() {
      return mediaId;
   }

   public void setMediaId(String mediaId) {
      this.mediaId = mediaId;
   }
}
