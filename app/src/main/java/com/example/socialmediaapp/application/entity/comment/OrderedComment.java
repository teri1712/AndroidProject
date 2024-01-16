package com.example.socialmediaapp.application.entity.comment;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.accesses.AccessSession;

@Entity(foreignKeys = {
        @ForeignKey(entity = CommentAccess.class,
                parentColumns = "id",
                childColumns = "commentAccessId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = AccessSession.class,
                parentColumns = "id",
                childColumns = "accessId",
                onDelete = ForeignKey.CASCADE)
})
public class OrderedComment {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private Integer ord;
   private long commentAccessId;
   private String commentId;
   private Integer accessId;

   public Integer getOrd() {
      return ord;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public void setOrd(Integer ord) {
      this.ord = ord;
   }

   public Integer getAccessId() {
      return accessId;
   }

   public void setAccessId(Integer accessId) {
      this.accessId = accessId;
   }

   public long getCommentAccessId() {
      return commentAccessId;
   }

   public void setCommentAccessId(long commentAccessId) {
      this.commentAccessId = commentAccessId;
   }

   public String getCommentId() {
      return commentId;
   }

   public void setCommentId(String commentId) {
      this.commentId = commentId;
   }

}
