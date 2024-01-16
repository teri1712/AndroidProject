package com.example.socialmediaapp.application.dao.post;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.post.ImagePost;
import com.example.socialmediaapp.application.entity.post.MediaPost;
import com.example.socialmediaapp.application.entity.post.Post;

import java.util.List;

@Dao
public abstract class PostDao {

   @Delete
   public abstract void deleteAll(List<Post> posts);

   @Query("delete from AccessSession where not exists(select * from OrderedPost where accessId = AccessSession.id) " +
           "and not exists(select * from UserProfile where  avatarPostAccessId = AccessSession.id or backgroundPostAccessId = AccessSession.id)")
   public abstract void deleteAllAccessOrphan();

   @Query("select * from Post where not exists(select * from OrderedPost where postId = Post.id) " +
           "and not exists(select * from UserProfile where  avatarPostId = Post.id or backgroundPostId = Post.id)")
   public abstract List<Post> findAllOrphan();

   @Query("select * from Post")
   public abstract List<Post> findAll();

   @Query("select * from Post where id = :postId")
   public abstract Post findPostById(String postId);

   @Query("select * from ImagePost where postId = :postId")
   public abstract ImagePost findImagePostByPostId(String postId);

   @Query("select * from MediaPost where postId = :postId")
   public abstract MediaPost findMediaPostByPostId(String postId);

   @Update
   public abstract void update(Post post);

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

   @Query("delete from Post where id = :id")
   public abstract void deleteById(String id);
}
