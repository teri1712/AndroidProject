package com.example.socialmediaapp.application.repo.core.tasks;

import java.util.LinkedList;
import java.util.Queue;


/* ensure serial event from UI layer */
public class SerialTaskScheduler implements Scheduler {

  public enum STATE {
    BUSY, FREE
  }

  private final Queue<Task<?>> qTask;
  private STATE current;

  public SerialTaskScheduler() {
    qTask = new LinkedList<>();
    current = STATE.FREE;
  }

  private void poll() {
    Task<?> task = qTask.remove();
    if (task == null) {
      current = STATE.FREE;
    } else {
      execute(task);
    }
  }

  private <T> void execute(Task<T> task) {
    current = STATE.BUSY;
    FutureResult<T> future = task.doTask();
    future.addListener(t -> {
      current = STATE.FREE;
      poll();
    });
  }

  @Override
  public <T> void schedule(Task<T> task) {
    if (current == STATE.FREE) {
      execute(task);
      return;
    }
    qTask.add(task);
  }

  @Override
  public void discardAll() {
    for (Task<?> task : qTask) {
      task.dispose();
    }
  }
}
