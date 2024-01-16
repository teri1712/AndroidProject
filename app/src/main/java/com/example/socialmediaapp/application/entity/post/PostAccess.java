package com.example.socialmediaapp.application.entity.post;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class PostAccess {
   @PrimaryKey
   private long id;

   public long getId() {
      return id;
   }
   public void setId(long id) {
      this.id = id;
   }
}
