package com.example.socialmediaapp.application.repo.core.tasks;

public interface TaskResult<T> {
  void onResult(T t);
}
