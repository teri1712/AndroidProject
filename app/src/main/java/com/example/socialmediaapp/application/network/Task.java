package com.example.socialmediaapp.application.network;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

public abstract class Task {
  private TaskDetails details;

  public Task() {
  }

  public Bundle getData() {
    return details.data;
  }

  @CallSuper
  protected void onTaskPrepare(TaskDetails details) {
    this.details = details;
  }

  public abstract void doTask();

  protected void onTaskCompleted() {
  }
}
