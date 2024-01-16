package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;

public class SessionProvider {
   public SessionProvider() {
   }

   public <T> SessionHandler create(T t) {
      if (t instanceof Post) {
         return new PostSessionHandler((Post) t);
      } else if (t instanceof Comment) {
         return new CommentSessionHandler((Comment) t);
      } else if (t instanceof ReplyComment) {
         return new ReplySessionHandler((ReplyComment) t);
      }
      return null;
   }

}
