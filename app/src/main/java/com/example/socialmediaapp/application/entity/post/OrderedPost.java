package com.example.socialmediaapp.application.entity.post;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.accesses.AccessSession;

@Entity(foreignKeys = {
        @ForeignKey(entity = PostAccess.class,
                parentColumns = "id",
                childColumns = "postAccessId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = AccessSession.class,
                parentColumns = "id",
                childColumns = "accessId",
                onDelete = ForeignKey.CASCADE)
}
)
public class OrderedPost {
   @PrimaryKey(autoGenerate = true)
   private Integer id;
   private long postAccessId;
   private String postId;

   @NonNull
   private Integer accessId;

   public Integer getId() {
      return id;
   }

   public String getPostId() {
      return postId;
   }

   @NonNull
   public Integer getAccessId() {
      return accessId;
   }

   public void setAccessId(@NonNull Integer accessId) {
      this.accessId = accessId;
   }

   public void setPostId(String postId) {
      this.postId = postId;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public long getPostAccessId() {
      return postAccessId;
   }

   public void setPostAccessId(long postAccessId) {
      this.postAccessId = postAccessId;
   }

}
