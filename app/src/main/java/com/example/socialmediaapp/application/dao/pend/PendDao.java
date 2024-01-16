package com.example.socialmediaapp.application.dao.pend;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.pend.PendRequest;
import com.example.socialmediaapp.application.entity.pend.PendTask;
import com.example.socialmediaapp.application.entity.pend.PendInput;

import java.util.List;

@Dao
public abstract class PendDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  public abstract void insertRequest(PendRequest request);

  @Insert
  public abstract long insert(PendTask pendTask);

  @Insert
  public abstract void insertAllInput(List<PendInput> inputs);

  @Delete
  public abstract int delete(PendTask pendTask);
  @Query("delete from PendRequest where id = :id")
  public abstract void deleteRequest(String id);

  @Query("select * from PendRequest")
  public abstract List<PendRequest> findAll();

  @Query("select * from PendTask where requestId = :id order by id")
  public abstract List<PendTask> findAllByRequest(String id);


  @Query("select * from PendInput where pendId = :pendId")
  public abstract List<PendInput> getInput(String pendId);

}
