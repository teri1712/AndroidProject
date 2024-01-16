package com.example.socialmediaapp.application.dao.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.user.UserProfile;

import java.util.List;

@Dao
public interface ProfileDao {

   @Insert
   void insert(UserProfile userProfile);

   @Update
   void update(UserProfile userProfile);

   @Query("select * from UserProfile")
   List<UserProfile> findAll();

   @Query("select * from UserProfile where id = :userId")
   UserProfile findById(String userId);

   @Delete
   void delete(UserProfile userProfile);

   @Query("delete from UserProfile")
   void deleteAll();
}
