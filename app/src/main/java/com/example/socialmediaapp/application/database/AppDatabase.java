package com.example.socialmediaapp.application.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.socialmediaapp.application.dao.CommentDao;
import com.example.socialmediaapp.application.dao.PostDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.entity.Post;
import com.example.socialmediaapp.application.entity.UserBasicInfo;

@Database(entities = {Post.class, UserBasicInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDao getPostDao();
    public abstract UserBasicInfoDao getUserBasicInfoDao();
    public abstract CommentDao getCommentDao();
}
