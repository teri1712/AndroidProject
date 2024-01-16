package com.example.socialmediaapp.application.network;

import android.os.Bundle;

public class TaskDetails {
  protected Bundle data;
  protected boolean willRestore;
  protected String id;
  protected PendMonitor monitor;
  public TaskDetails() {
    data = new Bundle();
    willRestore = false;
    monitor = new PendMonitor(data);
  }

  public String getId() {
    return id;
  }

  public PendMonitor getMonitor() {
    return monitor;
  }

  public Bundle getData() {
    return data;
  }

  public void setData(Bundle data) {
    this.data = data;
  }

}
