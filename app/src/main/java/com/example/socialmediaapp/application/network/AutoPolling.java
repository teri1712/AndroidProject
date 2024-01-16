package com.example.socialmediaapp.application.network;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class AutoPolling {
  private final UUID id;
  private final Queue<TaskRequest> tQueue;
  private final TaskScheduler scheduler;
  private boolean busy;

  public AutoPolling(
          UUID id,
          TaskScheduler scheduler) {
    this.id = id;
    this.scheduler = scheduler;
    this.tQueue = new LinkedList<>();
    this.busy = false;
  }

  /* This method also acquire the lock monitor */
  public void push(TaskRequest task) {
    tQueue.add(task);
    if (!busy) {
      poll();
    }
  }

  private void poll() {
    busy = true;
    TaskRequest task = tQueue.remove();
    if (task == null) {
      scheduler.onSerialRequestComplete(id);
      return;
    }
    scheduler.submit(() -> {
      task.execute();
      synchronized (scheduler.requestMap) {
        busy = false;
        poll();
      }
    });
  }
}
