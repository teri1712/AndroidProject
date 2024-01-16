package com.example.socialmediaapp.application.entity.pend;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.UUID;


@Entity(foreignKeys = @ForeignKey(
        entity = PendRequest.class,
        parentColumns = "id",
        childColumns = "requestId",
        onDelete = ForeignKey.CASCADE
))
public class PendTask {

  @PrimaryKey
  private String id;
  private String requestId;
  private String classTask;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getClassTask() {
    return classTask;
  }

  public void setClassTask(String classTask) {
    this.classTask = classTask;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }
}