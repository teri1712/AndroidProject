package com.example.socialmediaapp.application.dao.comment;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.reply.OrderedReply;
import com.example.socialmediaapp.application.entity.reply.ReplyCommentAccess;

import java.util.List;

@Dao
public interface OrderReplyDao {


   @Insert(onConflict = OnConflictStrategy.IGNORE)
   void insertReplyAccess(ReplyCommentAccess replyAccess);

   @Query("delete from ReplyCommentAccess")
   void deleteAllReplyAccess();

   @Query("delete from ReplyCommentAccess where id = :commentAccessId")
   void deleteReplyAccess(long commentAccessId);
   @Insert
   long insert(OrderedReply orderedComment);

   @Update
   void update(OrderedReply orderedComment);

   @Query("select * from OrderedReply where replyCommentAccessId = :replyCommentAccessId and ord > :lb order by ord asc limit :length")
   List<OrderedReply> findByBound(long replyCommentAccessId, Integer lb, Integer length);

   @Query("select * from OrderedReply where replyCommentAccessId = :replyCommentAccessId")
   List<OrderedReply> findAll(long replyCommentAccessId);

   @Query("select * from OrderedReply where accessId = :accessId and replyCommentAccessId = :replyCommentAccessId")
   OrderedReply find(long replyCommentAccessId, int accessId);

   @Query("select * from OrderedReply where replyCommentAccessId = :replyCommentAccessId and ord = (select min(ord) from OrderedReply where replyCommentAccessId = :replyCommentAccessId)")
   OrderedReply findFirst(long replyCommentAccessId);

   @Query("select * from OrderedReply where replyCommentAccessId = :replyCommentAccessId and ord = (select max(ord) from OrderedReply where replyCommentAccessId = :replyCommentAccessId)")
   OrderedReply findLast(long replyCommentAccessId);
}
