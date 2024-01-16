package com.example.socialmediaapp.application.dao.comment;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;

import java.util.List;

@Dao
public abstract class CommentDao {

   @Query("delete from AccessSession where not exists(select * from OrderedComment where accessId = AccessSession.id)")
   public abstract void deleteAllOrphanByOrderedComment();

   @Query("delete from AccessSession where not exists(select * from OrderedReply where accessId = AccessSession.id)")
   public abstract void deleteAllOrphanByOrderedReplyComment();

   @Query("select * from Comment")
   public abstract List<Comment> findAll();

   @Query("select * from Comment where id = :id")
   public abstract Comment findCommentById(String id);

   @Insert
   public abstract long insert(Comment comment);

   @Update
   public abstract void update(Comment comment);

   @Update
   public abstract void updateReplyComment(ReplyComment comment);

   @Delete
   public abstract void deleteComment(Comment comment);

   @Query("delete from Comment where id = :id")
   public abstract void deleteById(String id);

   @Query("delete from Comment where id = :id")
   public abstract void deleteReplyById(String id);

   @Query("delete from Comment")
   public abstract void deleteAllComment();

   //.------------------------------------------------------------------------------------.
   //.------------------------------------------------------------------------------------.

   @Query("select * from ReplyComment  where id = :id")
   public abstract ReplyComment findReplyCommentById(String id);

   @Insert
   public abstract long insertReplyComment(ReplyComment replyComment);

   @Query("delete from ReplyComment")
   public abstract void deleteAllReplyComment();

   @Delete
   public abstract void deleteReplyComment(ReplyComment comment);
}
