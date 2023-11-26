package com.example.socialmediaapp.container.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.socialmediaapp.container.entity.ImagePost;
import com.example.socialmediaapp.container.entity.MediaPost;
import com.example.socialmediaapp.container.entity.Post;

import java.util.List;

@Dao
public interface PostDao {

    @Query("select * from Post")
    List<Post> getPosts(int countLoaded);

    @Insert
    void insertAllPost(List<Post> post);

    @Delete
    void delete(Post post);

    @Insert
    void insertAllMediaPost(List<MediaPost> post);

    @Insert
    void insertAllImagePost(List<ImagePost> post);

    @Insert
    void insert(Post post);

    @Insert
    void insertMediaPost(MediaPost mediaPost);

    @Insert
    void insertImagePost(ImagePost imagePost);
    @Query("select * from ImagePost ip where ip.postId = :id")
    ImagePost findImagePostByPost(Integer id);

    @Query("select * from MediaPost mp where mp.postId = :id")
    MediaPost findMediaPostByPost(Integer id);

}
