package com.example.socialmediaapp.application.repo.core.utilities;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;

public class DataEmit {
  private String status;
  private String type;
  private Map<String, Object> data;

  public DataEmit(
          Map<String, Object> data,
          String status,
          String type) {
    this.status = status;
    this.data = data;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String getStatus() {
    return status;
  }

  public Map<String, Object> getData() {
    return data;
  }
}
