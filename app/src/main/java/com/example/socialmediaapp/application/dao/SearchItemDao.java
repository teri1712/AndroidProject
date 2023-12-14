package com.example.socialmediaapp.application.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.socialmediaapp.application.entity.ReplyComment;
import com.example.socialmediaapp.application.entity.SearchItem;

import java.util.List;

@Dao
public abstract class SearchItemDao {

   @Transaction
   public List<SearchItem> loadRecentSearchItems(String userAlias, Integer length) {
      SearchItem searchItem = findByAlias(userAlias);
      return findRecentSearchItemsByOrder(searchItem.getOrd(), length);
   }

   @Insert
   public abstract int insert(SearchItem searchItem);

   @Query("select * from SearchItem i,UserBasicInfo u where i.userInfoId = u.autoId and u.alias == :alias")
   public abstract SearchItem findByAlias(String alias);

   @Query("select * from SearchItem where ord = (select max(ord) from SearchItem)")
   public abstract ReplyComment findLastSearchItem();

   @Query("select * from SearchItem where ord > :ord  and ord <= (:ord +:length) ")
   public abstract List<SearchItem> findRecentSearchItemsByOrder(Integer ord, Integer length);

   @Query("select * from SearchItem ")
   public abstract List<SearchItem> findAll();
}
