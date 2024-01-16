package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataAccessHelper<T> {
  private String uuid;

  public DataAccessHelper(String uuid) {
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }

  public abstract List<T> loadFromLocal(Map<String, Object> query);

  public abstract Bundle loadFromServer() throws IOException;

  public abstract void cleanAll();

  public void pop(T lastItem) {
  }
}
