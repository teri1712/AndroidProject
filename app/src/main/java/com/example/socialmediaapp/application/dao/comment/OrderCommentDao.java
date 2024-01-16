package com.example.socialmediaapp.application.dao.comment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.comment.CommentAccess;
import com.example.socialmediaapp.application.entity.comment.OrderedComment;

import java.util.List;

@Dao
public abstract class OrderCommentDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  public abstract void insertCommentAccess(CommentAccess commentAccess);

  @Query("delete from CommentAccess where id = :commentAccessId")
  public abstract void deleteCommentAccess(long commentAccessId);

  @Query("delete from CommentAccess")
  public abstract void deleteAllCommentAccess();
  @Update
  public abstract void update(OrderedComment orderedComment);

  @Insert
  public abstract long insert(OrderedComment orderedComment);

  @Query("select * from OrderedComment where accessId = :accessId and commentAccessId = :commentAccessId")
  public abstract OrderedComment find(long commentAccessId, int accessId);

  @Query("select * from OrderedComment where commentAccessId = :commentAccessId and ord = (select max(ord) from OrderedComment where commentAccessId = :commentAccessId)")
  public abstract OrderedComment findLast(long commentAccessId);

  @Query("select * from Orderedcomment where commentAccessId = :cAccessId " +
          "and ord > (select ord from Orderedcomment o where o.accessId = :accessId and o.commentAccessId = :cAccessId)" +
          " order by ord asc limit :length")
  public abstract List<OrderedComment> findByOrder(long cAccessId, int accessId, Integer length);

  @Query("select * from Orderedcomment where commentAccessId = :cAccessId " +
          "order by ord asc limit :length")
  public abstract List<OrderedComment> findTopList(long cAccessId, Integer length);

  @Query("select * from Orderedcomment where commentAccessId = :commentAccessId")
  public abstract List<OrderedComment> findAll(long commentAccessId);

}
