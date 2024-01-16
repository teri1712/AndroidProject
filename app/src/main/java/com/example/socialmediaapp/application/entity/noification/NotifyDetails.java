package com.example.socialmediaapp.application.entity.noification;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotifyDetails {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private int cntUnRead;

   @NonNull
   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public int getCntUnRead() {
      return cntUnRead;
   }

   public void setCntUnRead(int cntUnRead) {
      this.cntUnRead = cntUnRead;
   }
}
