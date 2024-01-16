package com.example.socialmediaapp.application.session;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.example.socialmediaapp.application.network.Task;

public class SessionTask extends Task {
  protected SessionHandler handler;

  public SessionTask() {
    super();
  }

  public void setHandler(SessionHandler handler) {
    this.handler = handler;
  }

  @Override
  public void doTask() {

  }

  @CallSuper
  @Override
  protected void onTaskCompleted() {
    super.onTaskCompleted();
    if (handler == null) return;
    synchronized (handler.lock) {
      handler.countEnqueued--;
      handler.lock.notify();
    }
  }
}
