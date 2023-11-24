package com.example.socialmediaapp.container.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.container.entity.Comment;
import com.example.socialmediaapp.container.entity.ImagePost;
import com.example.socialmediaapp.container.entity.MediaPost;
import com.example.socialmediaapp.container.entity.Post;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("select * from Comment")
    List<Comment> getComments();
    @Insert
    void insertAll(List<Comment> comments);
    @Delete
    void delete(Comment comment);
    @Insert
    void insert(Comment comment);

}
