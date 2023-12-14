package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.socialmediaapp.application.entity.Comment;
import com.example.socialmediaapp.application.entity.ReplyComment;

import java.util.List;

@Dao
public abstract class CommentDao {

   @Transaction
   public List<Comment> loadComments(Integer lastId, Integer sessionId, Integer length) {
      Comment comment = findCommentById(lastId, sessionId);
      int order = comment.getOrd();
      return findCommentsByOrder(order, length, sessionId);
   }

   @Query("select * from Comment where sessionId = :sessionId and ord = (select max(ord) from Comment where sessionId = :sessionId)")
   public abstract Comment findLastComment(Integer sessionId);

   @Query("select * from Comment where ord > :ord and ord <= (:ord + :length) and sessionId = :sessionId order by ord asc")
   public abstract List<Comment> findCommentsByOrder(int ord, int length, Integer sessionId);

   @Query("select * from Comment where sessionId = :sessionId")
   public abstract List<Comment> findAllBySession(Integer sessionId);

   @Query("select * from Comment where sessionId = :sessionId and id = :commentId")
   public abstract Comment findCommentById(Integer commentId, Integer sessionId);

   @Query("select * from ReplyComment where sessionId = :sessionId")
   public abstract List<ReplyComment> findAllReplyBySession(Integer sessionId);

   @Insert
   public abstract long insert(Comment comment);

   @Delete
   public abstract void deleteComment(Comment comment);

   @Query("delete from Comment")
   public abstract void deleteAllComment();
   //.------------------------------------------------------------------------------------.
   //.------------------------------------------------------------------------------------.

   @Transaction
   public List<ReplyComment> loadReplyComments(Integer commentId, Integer sessionId, Integer length) {
      ReplyComment replyComment = findReplyCommentById(commentId, sessionId);
      int ord = replyComment.getOrd();
      return findReplyCommentsByOrder(ord, sessionId,length);
   }

   @Query("select * from ReplyComment where sessionId = :sessionId and ord = (select max(ord) from ReplyComment where sessionId = :sessionId)")
   public abstract ReplyComment findLastReplyComment(Integer sessionId);

   @Query("select * from ReplyComment where sessionId = :sessionId and id = :commentId")
   public abstract ReplyComment findReplyCommentById(Integer commentId, Integer sessionId);

   @Query("select * from ReplyComment where ord > :ord and sessionId = :sessionId order by ord asc limit :length")
   public abstract List<ReplyComment> findReplyCommentsByOrder(int ord, Integer sessionId,Integer length);

   @Insert
   public abstract void insertReplyComment(ReplyComment replyComment);

   @Query("delete from ReplyComment")
   public abstract void deleteAllReplyComment();
   @Delete
   public abstract void deleteReplyComment(ReplyComment comment);
}
