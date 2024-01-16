package com.example.socialmediaapp.application.entity.accesses;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"alias", "itemId"})
public class AccessRegistry {
   @NonNull
   private String alias;
   @NonNull
   private String itemId;
   @NonNull
   public String getAlias() {
      return alias;
   }

   public void setAlias(@NonNull String alias) {
      this.alias = alias;
   }

   @NonNull
   public String getItemId() {
      return itemId;
   }

   public void setItemId(@NonNull String itemId) {
      this.itemId = itemId;
   }
}
