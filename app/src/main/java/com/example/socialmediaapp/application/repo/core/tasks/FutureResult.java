package com.example.socialmediaapp.application.repo.core.tasks;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

public class FutureResult<T> {
  private final List<TaskResult<T>> listeners;
  private T cache;

  public FutureResult() {
    listeners = new ArrayList<>();
  }

  public void onComplete(T res) {
    if (cache != null) return;
    cache = res;
    for (TaskResult<T> l : listeners) {
      l.onResult(res);
    }
    listeners.clear();
  }

  public void postComplete(T res) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(() -> onComplete(res));
  }

  public void addListener(TaskResult<T> l) {
    if (cache != null) {
      l.onResult(cache);
      return;
    }
    listeners.add(l);
  }
}
