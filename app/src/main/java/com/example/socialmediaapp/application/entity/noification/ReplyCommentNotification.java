package com.example.socialmediaapp.application.entity.noification;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = NotificationItem.class
        , parentColumns = "id"
        , childColumns = "notiId"
        , onDelete = ForeignKey.CASCADE))
public class ReplyCommentNotification {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private String notiId;
   private Integer sessionId;
   private String replyCommentId;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getSessionId() {
      return sessionId;
   }

   public void setSessionId(Integer sessionId) {
      this.sessionId = sessionId;
   }

   public String getNotiId() {
      return notiId;
   }

   public String getReplyCommentId() {
      return replyCommentId;
   }

   public void setReplyCommentId(String replyCommentId) {
      this.replyCommentId = replyCommentId;
   }

   public void setNotiId(String notiId) {
      this.notiId = notiId;
   }
}
