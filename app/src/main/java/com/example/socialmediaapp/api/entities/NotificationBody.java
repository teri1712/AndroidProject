package com.example.socialmediaapp.api.entities;

public class NotificationBody {
   private String id;
   private UserBasicInfoBody from;
   private String receiver;
   private String content;
   private Long time;
   private Boolean read;
   private String type;
   private CommentNotificationBody commentBody;
   private FriendRequestBody friendRequestBody;

   public NotificationBody() {
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getReceiver() {
      return receiver;
   }

   public void setReceiver(String receiver) {
      this.receiver = receiver;
   }

   public UserBasicInfoBody getFrom() {
      return from;
   }

   public void setFrom(UserBasicInfoBody from) {
      this.from = from;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public Boolean getRead() {
      return read;
   }

   public void setRead(Boolean read) {
      this.read = read;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public CommentNotificationBody getCommentBody() {
      return commentBody;
   }

   public void setCommentBody(CommentNotificationBody commentBody) {
      this.commentBody = commentBody;
   }

   public FriendRequestBody getFriendRequestBody() {
      return friendRequestBody;
   }

   public void setFriendRequestBody(FriendRequestBody friendRequestBody) {
      this.friendRequestBody = friendRequestBody;
   }
}
