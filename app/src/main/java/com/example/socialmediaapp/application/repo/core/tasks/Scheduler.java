package com.example.socialmediaapp.application.repo.core.tasks;

/* */
public interface Scheduler {
  <T> void schedule(Task<T> task);

  void discardAll();
}
