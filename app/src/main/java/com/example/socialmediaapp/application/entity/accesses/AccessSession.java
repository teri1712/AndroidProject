package com.example.socialmediaapp.application.entity.accesses;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = AccessRegistry.class,
        parentColumns = {"alias", "itemId"},
        childColumns = {"registryAlias", "itemId"},
        onDelete = ForeignKey.CASCADE
))
public class AccessSession {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private String registryAlias;
   private String itemId;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getRegistryAlias() {
      return registryAlias;
   }

   public void setRegistryAlias(String registryAlias) {
      this.registryAlias = registryAlias;
   }

   public String getItemId() {
      return itemId;
   }

   public void setItemId(String itemId) {
      this.itemId = itemId;
   }
}
