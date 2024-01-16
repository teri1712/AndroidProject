package com.example.socialmediaapp.application.entity.reply;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.accesses.AccessSession;

@Entity(foreignKeys = {
        @ForeignKey(entity = ReplyCommentAccess.class,
                parentColumns = "id",
                childColumns = "replyCommentAccessId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = AccessSession.class,
                parentColumns = "id",
                childColumns = "accessId",
                onDelete = ForeignKey.CASCADE)
})
public class OrderedReply {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private Integer ord;
   private long replyCommentAccessId;
   private String replyCommentId;
   private Integer accessId;

   public Integer getOrd() {
      return ord;
   }

   public void setOrd(Integer ord) {
      this.ord = ord;
   }

   public long getReplyCommentAccessId() {
      return replyCommentAccessId;
   }

   public Integer getId() {
      return id;
   }

   public Integer getAccessId() {
      return accessId;
   }

   public void setAccessId(Integer accessId) {
      this.accessId = accessId;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public void setReplyCommentAccessId(long replyCommentAccessId) {
      this.replyCommentAccessId = replyCommentAccessId;
   }

   public String getReplyCommentId() {
      return replyCommentId;
   }


   public void setReplyCommentId(String replyCommentId) {
      this.replyCommentId = replyCommentId;
   }

}
