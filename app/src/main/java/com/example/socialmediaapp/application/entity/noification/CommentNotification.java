package com.example.socialmediaapp.application.entity.noification;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.accesses.AccessSession;
import com.example.socialmediaapp.application.entity.noification.NotificationItem;

@Entity(foreignKeys = {@ForeignKey(entity = NotificationItem.class
        , parentColumns = "id"
        , childColumns = "notiId"
        , onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = AccessSession.class,
                parentColumns = "id",
                childColumns = "accessId",
                onDelete = ForeignKey.CASCADE)})
public class CommentNotification {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private String notiId;
   private Integer accessId;
   private String postId;
   private String commentId;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getNotiId() {
      return notiId;
   }


   public String getCommentId() {
      return commentId;
   }

   public Integer getAccessId() {
      return accessId;
   }

   public String getPostId() {
      return postId;
   }

   public void setPostId(String postId) {
      this.postId = postId;
   }

   public void setAccessId(Integer accessId) {
      this.accessId = accessId;
   }

   public void setCommentId(String commentId) {
      this.commentId = commentId;
   }

   public void setNotiId(String notiId) {
      this.notiId = notiId;
   }
}
