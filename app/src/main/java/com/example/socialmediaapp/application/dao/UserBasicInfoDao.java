package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.UserBasicInfo;

import java.util.List;

@Dao
public interface UserBasicInfoDao {
   @Query("select * from UserBasicInfo where autoId = :autoId ")
   UserBasicInfo findUserBasicInfo(Integer autoId);

   @Insert
   long insert(UserBasicInfo userBasicInfo);

   @Insert
   void insertAll(List<UserBasicInfo> userBasicInfos);

   @Query("delete from UserBasicInfo")
   void deleteAll(List<UserBasicInfo> users);

   @Query("delete from UserBasicInfo where autoId = :autoId")
   int deleteById(Integer autoId);

   @Query("delete from UserBasicInfo")
   void deleteAll();

   @Delete
   void delete(UserBasicInfo userBasicInfo);

}
