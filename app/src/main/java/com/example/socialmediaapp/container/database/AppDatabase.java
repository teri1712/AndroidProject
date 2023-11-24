package com.example.socialmediaapp.container.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.socialmediaapp.container.dao.CommentDao;
import com.example.socialmediaapp.container.dao.PostDao;
import com.example.socialmediaapp.container.dao.UserBasicInfoDao;
import com.example.socialmediaapp.container.entity.Post;
import com.example.socialmediaapp.container.entity.UserBasicInfo;

@Database(entities = {Post.class, UserBasicInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDao getPostDao();
    public abstract UserBasicInfoDao getUserBasicInfoDao();
    public abstract CommentDao getCommentDao();
}
