package com.example.socialmediaapp.application.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.UserBasicInfo;

import java.util.List;

@Dao
public interface UserBasicInfoDao {
    @Query("select * from UserBasicInfo where id = :id")
    UserBasicInfo findUserBasicInfoById(Integer id);

    @Insert
    long insert(UserBasicInfo userBasicInfo);

    @Insert
    void insertAll(List<UserBasicInfo> userBasicInfos);


    @Query("delete from UserBasicInfo where sessionId = :sessionId")
    void deleteAll(Integer sessionId);

    @Query("select * from UserBasicInfo where sessionId = :sessionId")
    List<UserBasicInfo> getRecentSearchItems(Integer sessionId);
}
