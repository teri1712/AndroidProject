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
public interface PostDao {

    @Query("select * from Post where ord > :countLoaded and sessionId = :sessionId")
    List<Post> getPosts(int countLoaded,Integer sessionId);

    @Query("delete from Post where sessionId = :sessionId")
    void deleteAllPost(Integer sessionId);
    @Query("delete from ImagePost where sessionId = :sessionId")
    void deleteAllImagePost(Integer sessionId);
    @Query("delete from MediaPost where sessionId = :sessionId")
    void deleteAllMediaPost(Integer sessionId);

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
