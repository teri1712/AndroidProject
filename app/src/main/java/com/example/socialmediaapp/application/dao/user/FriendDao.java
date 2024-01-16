package com.example.socialmediaapp.application.dao.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.util.List;

@Dao
public abstract class FriendDao {

   @Transaction
   public List<FriendRequestItem> loadFriendRequestItems(String userId, Integer length) {
      int ord = Integer.MIN_VALUE;
      if (userId != null) {
         FriendRequestItem friendRequestItem = findByUserId(userId);
         ord = friendRequestItem.getId();
      }
      return findFriendRequestItemsByOrder(ord, length);
   }

   @Insert
   public abstract long insert(FriendRequestItem requestItem);

   @Query("select count(*) from FriendRequestItem")
   public abstract int count();

   @Query("select * from UserBasicInfo u where exists(select * from FriendRequestItem i where i.id = :searchId and i.userInfoId = u.autoId)")
   public abstract UserBasicInfo findByUserByFriendRequestId(Integer searchId);

   @Query("select * from FriendRequestItem i where exists(" +
           "select * from UserBasicInfo u " +
           "where u.id = :userId and i.userInfoId = u.autoId)")
   public abstract FriendRequestItem findByUserId(String userId);

   @Query("select * from FriendRequestItem where id = (select max(id) from FriendRequestItem)")
   public abstract FriendRequestItem findLastFriendRequestItem();

   @Query("select * from FriendRequestItem where id > :ord order by id limit :length")
   public abstract List<FriendRequestItem> findFriendRequestItemsByOrder(Integer ord, Integer length);

   @Query("select * from FriendRequestItem")
   public abstract List<FriendRequestItem> findAll();

   @Query("delete from FriendRequestItem")
   public abstract void deleteAll();
}
