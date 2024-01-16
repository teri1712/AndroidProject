package com.example.socialmediaapp.application.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.socialmediaapp.application.entity.SequenceTable;

@Dao
public abstract class SequenceDao {

   @Insert
   public abstract void insert(SequenceTable sequenceTable);
   @Query("select * from SequenceTable")
   public abstract SequenceTable get();
   @Update
   public abstract void update(SequenceTable sequenceTable);

   @Transaction
   public int getTailValue() {
      SequenceTable sequenceTable = get();
      int value = sequenceTable.getTail() + 1;
      sequenceTable.setTail(value);
      update(sequenceTable);
      return value;
   }

   @Transaction
   public int getHeadValue() {
      SequenceTable sequenceTable = get();
      int value = sequenceTable.getHead() - 1;
      sequenceTable.setHead(value);
      update(sequenceTable);
      return value;
   }
}
