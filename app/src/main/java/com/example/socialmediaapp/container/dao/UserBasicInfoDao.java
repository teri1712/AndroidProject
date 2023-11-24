package com.example.socialmediaapp.container.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.socialmediaapp.container.entity.Post;
import com.example.socialmediaapp.container.entity.UserBasicInfo;

import java.util.Iterator;
import java.util.List;

@Dao
public interface UserBasicInfoDao {
    @Query("select * from UserBasicInfo where id = :id")
    UserBasicInfo findUserBasicInfoById(Integer id);

    @Insert
    void insertAllUserBasicInfo(List<UserBasicInfo> userBasicInfo);

    @Insert
    void insert(UserBasicInfo userBasicInfo);

    @Delete
    void delete(UserBasicInfo userBasicInfo);
}
