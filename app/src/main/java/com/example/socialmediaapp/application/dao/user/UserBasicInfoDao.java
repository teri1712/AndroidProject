package com.example.socialmediaapp.application.dao.user;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.util.List;

@Dao
public interface UserBasicInfoDao {
   @Query("select * from UserBasicInfo where autoId = :autoId ")
   UserBasicInfo findUser(Integer autoId);

   @Insert
   long insert(UserBasicInfo userBasicInfo);

   @Insert
   void insertAll(List<UserBasicInfo> userBasicInfos);

   @Query("delete from UserBasicInfo")
   void deleteAll();

   @Query("delete from UserBasicInfo where autoId = :autoId")
   int deleteById(Integer autoId);

   @Delete
   void delete(UserBasicInfo userBasicInfo);

}
