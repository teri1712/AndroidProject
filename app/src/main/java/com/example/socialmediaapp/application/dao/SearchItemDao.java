package com.example.socialmediaapp.application.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.socialmediaapp.application.entity.SearchItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.util.List;

@Dao
public abstract class SearchItemDao {

   @Transaction
   public List<SearchItem> loadRecentSearchItems(String userId, Integer length) {
      int ord = Integer.MIN_VALUE;
      if (userId != null) {
         SearchItem searchItem = findByUserId(userId);
         ord = searchItem.getId();
      }
      return findRecentSearchItemsByOrder(ord, length);
   }

   @Insert
   public abstract long insert(SearchItem searchItem);

   @Query("select * from UserBasicInfo u where exists(select * from SearchItem i where i.id = :searchId and i.userInfoId = u.autoId)")
   public abstract UserBasicInfo findByUserBySearchId(Integer searchId);

   @Query("select * from SearchItem i where exists(select * from UserBasicInfo u where u.id = :userId and i.userInfoId = u.autoId)")
   public abstract SearchItem findByUserId(String userId);

   @Query("select * from SearchItem where id = (select max(id) from SearchItem)")
   public abstract SearchItem findLastSearchItem();

   @Query("select * from SearchItem where id > :ord order by id asc limit :length")
   public abstract List<SearchItem> findRecentSearchItemsByOrder(Integer ord, Integer length);

   @Query("select * from SearchItem")
   public abstract List<SearchItem> findAll();
}
