package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;

public class ItemInfoProvider {
   public <T> String getItemAlias(T t) {
      if (t instanceof Post) {
         return "Post";
      } else if (t instanceof Comment) {
         return "Comment";
      } else if (t instanceof ReplyComment) {
         return "ReplyComment";
      }
      return null;
   }

   public <T> String getItemIdentity(T t) {
      if (t instanceof Post) {
         return ((Post) t).getId();
      } else if (t instanceof Comment) {
         return ((Comment) t).getId();
      } else if (t instanceof ReplyComment) {
         return ((ReplyComment) t).getId();
      }
      return null;
   }
}
