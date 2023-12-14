package com.example.socialmediaapp.application.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.socialmediaapp.application.dao.ChatDao;
import com.example.socialmediaapp.application.dao.CommentDao;
import com.example.socialmediaapp.application.dao.MessageDao;
import com.example.socialmediaapp.application.dao.PostDao;
import com.example.socialmediaapp.application.dao.SearchItemDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.entity.Chat;
import com.example.socialmediaapp.application.entity.Comment;
import com.example.socialmediaapp.application.entity.ImageMessageItem;
import com.example.socialmediaapp.application.entity.ImagePost;
import com.example.socialmediaapp.application.entity.IconMessageItem;
import com.example.socialmediaapp.application.entity.MediaPost;
import com.example.socialmediaapp.application.entity.MessageItem;
import com.example.socialmediaapp.application.entity.Post;
import com.example.socialmediaapp.application.entity.ReplyComment;
import com.example.socialmediaapp.application.entity.SearchItem;
import com.example.socialmediaapp.application.entity.SequenceTable;
import com.example.socialmediaapp.application.entity.TextMessageItem;
import com.example.socialmediaapp.application.entity.UserBasicInfo;

@Database(entities = {Post.class
        , MediaPost.class
        , ImagePost.class
        , UserBasicInfo.class
        , Comment.class
        , ReplyComment.class
        , SearchItem.class
        , MessageItem.class
        , TextMessageItem.class
        , ImageMessageItem.class
        , IconMessageItem.class
        , Chat.class
        , SequenceTable.class
        , OrderedChat.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDao getPostDao();

    public abstract UserBasicInfoDao getUserBasicInfoDao();

    public abstract CommentDao getCommentDao();

    public abstract SearchItemDao getSearchItemDao();

    public abstract ChatDao getChatDao();

    public abstract MessageDao getMessageDao();

    public abstract SequenceDao getSequenceDao();
}
