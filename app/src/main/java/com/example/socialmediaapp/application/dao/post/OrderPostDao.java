package com.example.socialmediaapp.application.dao.post;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.post.OrderedPost;
import com.example.socialmediaapp.application.entity.post.PostAccess;

import java.util.List;

@Dao
public interface OrderPostDao {

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   void insertPostAccess(PostAccess postAccess);

   @Query("delete from PostAccess where id = :postAccessId")
   void deletePostAccess(long postAccessId);
   @Query("delete from PostAccess")
   void deleteAllPostAccess();

   @Insert
   void insert(OrderedPost orderedPost);
   @Delete
   void delete(OrderedPost orderedPost);
   @Query("select * from OrderedPost where accessId = :accessId and postAccessId = :postAccessId")
   OrderedPost find(long postAccessId, int accessId);
   @Query("select * from OrderedPost where postAccessId = :postAccessId and id = (select max(id) from OrderedPost where postAccessId = :postAccessId)")
   OrderedPost findLast(long postAccessId);
   @Query("select * from OrderedPost where postAccessId = :postAccessId and id > :lb order by id asc limit :length")
   List<OrderedPost> findByBound(long postAccessId, Integer lb, Integer length);

   @Query("select * from OrderedPost where postAccessId = :postAccessId and id <= :bound")
   List<OrderedPost> findByUpperBound(long postAccessId, Integer bound);

   @Query("select * from OrderedPost where postAccessId = :postAccessId")
   List<OrderedPost> findAll(long postAccessId);
}
