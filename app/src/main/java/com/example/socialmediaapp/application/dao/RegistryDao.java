package com.example.socialmediaapp.application.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.socialmediaapp.application.entity.accesses.AccessRegistry;
import com.example.socialmediaapp.application.entity.accesses.AccessSession;

import java.util.List;

@Dao
public abstract class RegistryDao {
   @Query("select * from AccessSession where registryAlias = :alias and itemId = :itemId")
   public abstract List<AccessSession> loadAll(String alias, String itemId);

   @Query("select * from AccessRegistry where alias = :alias " +
           "and not exists(select * from AccessSession where AccessSession.registryAlias = :alias " +
           "and AccessSession.itemId = AccessRegistry.itemId)")
   public abstract List<AccessRegistry> findAllOrphanByAlias(String alias);

   @Query("select * from AccessRegistry where alias = :alias and itemId = :itemId")
   public abstract AccessRegistry findById(String alias, String itemId);

   @Delete
   public abstract int deleteAccessSession(AccessSession accessSession);

   @Delete
   public abstract int deleteAccessRegistry(AccessRegistry registry);

   @Query("delete from AccessRegistry")
   public abstract void deleteAll();

   @Delete
   public abstract void deleteListRegistry(List<AccessRegistry> registries);


   @Insert
   public abstract long insertAccessSession(AccessSession accessSession);

   @Insert
   public abstract void insertAccessRegistry(AccessRegistry registry);

}
