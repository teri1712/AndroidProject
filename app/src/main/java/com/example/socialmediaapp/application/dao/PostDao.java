package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.ImagePost;
import com.example.socialmediaapp.application.entity.MediaPost;
import com.example.socialmediaapp.application.entity.Post;

import java.util.List;

@Dao
public abstract class PostDao {
  @Query("select * from Post where sessionId = :sessionId and ord = (select max(ord) from Post where sessionId = :sessionId)")
  public abstract Post findLastPostOfUser(Integer sessionId);

  @Query("select * from Post where sessionId = :sessionId and ord > :lastOrder order by ord asc limit :length")
  public abstract List<Post> loadPostsByOrder(int lastOrder, Integer sessionId, Integer length);

  @Query("select * from Post where sessionId = :sessionId")
  public abstract List<Post> findAllBySession(Integer sessionId);

  @Query("select * from Post where sessionId = :sessionId and ord <= :bound")
  public abstract List<Post> findAllPostByBound(Integer sessionId, Integer bound);

  @Query("select * from Post where id=:postId and sessionId = :sessionId")
  public abstract Post findPostById(Integer postId, Integer sessionId);

  @Query("select * from ImagePost where postId = :postId")
  public abstract ImagePost findImagePostByPostId(Integer postId);

  @Query("select * from MediaPost where postId = :postId")
  public abstract MediaPost findMediaPostByPostId(Integer postId);

  @Insert
  public abstract long insert(Post post);

  @Insert
  public abstract void insertMediaPost(MediaPost mediaPost);

  @Insert
  public abstract void insertImagePost(ImagePost imagePost);

  @Query("delete from Post")
  public abstract void deleteAllPost();

  @Delete
  public abstract void deletePost(Post post);
}
