package com.example.socialmediaapp.application.entity.comment;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.application.entity.pend.PendTask;

@Entity(foreignKeys = @ForeignKey(
        entity = PendTask.class,
        parentColumns = "id",
        childColumns = "pendId"
))
public class PendComment {
  @PrimaryKey
  private Integer id;

  @NonNull
  private Integer pendId;
  private String content;
  private String imageUri;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @NonNull
  public Integer getPendId() {
    return pendId;
  }

  public void setPendId(@NonNull Integer pendId) {
    this.pendId = pendId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImageUri() {
    return imageUri;
  }

  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }
}
