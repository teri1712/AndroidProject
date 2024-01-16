package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.noification.CommentNotification;
import com.example.socialmediaapp.application.entity.noification.FriendRequestNotification;
import com.example.socialmediaapp.application.entity.noification.NotificationItem;
import com.example.socialmediaapp.application.entity.noification.NotifyDetails;
import com.example.socialmediaapp.application.entity.noification.ReplyCommentNotification;

import java.util.List;

@Dao
public interface NotificationDao {

   @Query("delete from NotifyDetails")
   void deleteNotifyDetails();

   @Update
   void updateNotifyDetails(NotifyDetails notifyDetails);

   @Insert
   long insertNotifyDetails(NotifyDetails notifyDetails);

   @Query("select * from NotifyDetails")
   NotifyDetails findNotifyDetails();

   @Insert
   void insert(NotificationItem notificationItem);

   @Insert
   void insertFReqNotify(FriendRequestNotification notificationItem);

   @Insert
   void insertCommentNotify(CommentNotification notificationItem);

   @Insert
   void insertReplyCommentNotification(ReplyCommentNotification notificationItem);

   @Insert
   void insertAll(List<NotificationItem> notificationItem);

   @Query("delete from NotificationItem")
   void deleteAll();

   @Query("select * from NotificationItem where time < :time order by time desc limit 8")
   List<NotificationItem> loadNotifyByTime(long time);

   @Query("select * from FriendRequestNotification where notiId = :notiId")
   FriendRequestNotification findFReqNotifyByNotiId(String notiId);

   @Query("select * from CommentNotification where notiId = :notiId")
   CommentNotification findCommentNotifyByNotiId(String notiId);

   @Query("select id from NotificationItem where time = (select min(time) from NotificationItem)")
   String findLastInLocal();

   @Query("select *  from NotificationItem where time = (select max(time) from NotificationItem)")
   NotificationItem findFirstInLocal();

   @Query("select * from NotificationItem where id = :id")
   NotificationItem findById(String id);

   @Query("update NotificationItem set isRead = 1 where isRead = 0")
   void updateAllRead();

   @Query("select id from NotificationItem where isRead = 1 and time = (select max(time) from NotificationItem where isRead = 1)")
   String getLastReadId();

}
