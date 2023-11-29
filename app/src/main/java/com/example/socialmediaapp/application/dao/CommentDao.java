package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("select * from Comment where ord > :countLoaded and sessionId = :sessionId")
    List<Comment> getComments(int countLoaded, Integer sessionId);

    @Insert
    void insert(Comment comment);

    @Query("delete from Comment where sessionId = :sessionId")
    void deleteAll(Integer sessionId);


}
