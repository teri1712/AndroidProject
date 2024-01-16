package com.example.socialmediaapp.application.repo.core.tasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class Task<T> {
  private final MutableLiveData<T> uiCallBack;

  public Task() {
    this.uiCallBack = new MutableLiveData<>();
  }

  public FutureResult<T> doTask() {
    FutureResult<T> future = onDoTask();
    future.addListener(new TaskResult<T>() {
      @Override
      public void onResult(T t) {
        uiCallBack.setValue(t);
      }
    });
    return future;
  }

  public void dispose() {
    uiCallBack.setValue(null);
  }

  protected abstract FutureResult<T> onDoTask();

  public LiveData<T> getCallBack() {
    return uiCallBack;
  }
}
