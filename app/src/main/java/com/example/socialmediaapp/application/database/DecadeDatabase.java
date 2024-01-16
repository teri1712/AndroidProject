package com.example.socialmediaapp.application.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.socialmediaapp.application.dao.message.ChatDao;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.dao.pend.PendDao;
import com.example.socialmediaapp.application.dao.user.FriendDao;
import com.example.socialmediaapp.application.dao.message.MessageDao;
import com.example.socialmediaapp.application.dao.NotificationDao;
import com.example.socialmediaapp.application.dao.comment.OrderCommentDao;
import com.example.socialmediaapp.application.dao.post.OrderPostDao;
import com.example.socialmediaapp.application.dao.comment.OrderReplyDao;
import com.example.socialmediaapp.application.dao.post.PostDao;
import com.example.socialmediaapp.application.dao.user.ProfileDao;
import com.example.socialmediaapp.application.dao.RegistryDao;
import com.example.socialmediaapp.application.dao.SearchItemDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.entity.accesses.AccessRegistry;
import com.example.socialmediaapp.application.entity.accesses.AccessSession;
import com.example.socialmediaapp.application.entity.pend.PendRequest;
import com.example.socialmediaapp.application.entity.pend.PendTask;
import com.example.socialmediaapp.application.entity.pend.PendInput;
import com.example.socialmediaapp.application.entity.noification.ReplyCommentNotification;
import com.example.socialmediaapp.application.entity.message.Chat;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.comment.CommentAccess;
import com.example.socialmediaapp.application.entity.noification.CommentNotification;
import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.entity.noification.FriendRequestNotification;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.post.ImagePost;
import com.example.socialmediaapp.application.entity.message.IconMessageItem;
import com.example.socialmediaapp.application.entity.post.MediaPost;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.noification.NotificationItem;
import com.example.socialmediaapp.application.entity.noification.NotifyDetails;
import com.example.socialmediaapp.application.entity.comment.OrderedComment;
import com.example.socialmediaapp.application.entity.post.OrderedPost;
import com.example.socialmediaapp.application.entity.reply.OrderedReply;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.post.PostAccess;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.entity.reply.ReplyCommentAccess;
import com.example.socialmediaapp.application.entity.SearchItem;
import com.example.socialmediaapp.application.entity.SequenceTable;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.application.entity.user.UserProfile;

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
        , OrderedChat.class
        , PendTask.class
        , PostAccess.class
        , OrderedPost.class
        , CommentAccess.class
        , OrderedComment.class
        , ReplyCommentAccess.class
        , OrderedReply.class
        , FriendRequestItem.class
        , AccessSession.class
        , AccessRegistry.class
        , UserProfile.class
        , NotifyDetails.class
        , NotificationItem.class
        , FriendRequestNotification.class
        , CommentNotification.class
        , ReplyCommentNotification.class
        , PendTask.class
        , PendInput.class
        , PendRequest.class
}, version = 36)
public abstract class DecadeDatabase extends RoomDatabase {
  private static DecadeDatabase database;

  public static DecadeDatabase getInstance() {
    return database;
  }

  public static void initDecadeDatabase(Context context) {
    database = Room.databaseBuilder(context
                    , DecadeDatabase.class
                    , "MyApp")
            .fallbackToDestructiveMigration()
            .build();
  }

  public abstract PostDao getPostDao();

  public abstract UserBasicInfoDao getUserBasicInfoDao();

  public abstract CommentDao getCommentDao();

  public abstract SearchItemDao getSearchItemDao();

  public abstract ChatDao getChatDao();

  public abstract MessageDao getMessageDao();

  public abstract SequenceDao getSequenceDao();

  public abstract FriendDao getFriendDao();

  public abstract NotificationDao getNotificationDao();

  public abstract RegistryDao getRegistryDao();

  public abstract ProfileDao getProfileDao();

  public abstract OrderCommentDao getOrderCommentDao();

  public abstract OrderPostDao getOrderPostDao();

  public abstract OrderReplyDao getOrderReplyDao();

  public abstract PendDao getPendDao();
}
