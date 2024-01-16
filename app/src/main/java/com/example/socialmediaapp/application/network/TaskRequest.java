package com.example.socialmediaapp.application.network;

import android.os.Bundle;

import androidx.annotation.CallSuper;

import com.example.socialmediaapp.application.dao.pend.PendDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.pend.PendInput;
import com.example.socialmediaapp.application.entity.pend.PendTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class TaskRequest {
  protected UUID reqId;
  protected TaskScheduler scheduler;
  protected Task task;
  protected TaskDetails details;
  protected PendTask pendTask;
  protected PendDao dao;

  /* for restoration */
  protected TaskRequest(
          TaskScheduler scheduler,
          PendTask pendTask) throws
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          IllegalAccessException,
          InstantiationException {
    this.scheduler = scheduler;
    Bundle data = new Bundle();

    dao = DecadeDatabase.getInstance().getPendDao();
    List<PendInput> inputList = dao.getInput(pendTask.getId());
    for (PendInput input : inputList) {
      String key = input.getKey();
      String value = input.getValue();
      Class<?> clazz = Class.forName(input.getClassName());
      if (clazz.isAssignableFrom(Integer.class)) {
        data.putInt(key, Integer.parseInt(value));
      } else if (clazz.isAssignableFrom(Long.class)) {
        data.putLong(key, Long.parseLong(value));
      } else if (clazz.isAssignableFrom(String.class)) {
        data.putString(key, value);
      }
    }
    details = new TaskDetails();
    details.data = data;
    details.willRestore = true;
    details.id = pendTask.getId();

    Class<?> tClass = Class.forName(pendTask.getClassTask());
    Constructor<?> c = tClass.getConstructor();
    task = (Task) c.newInstance();
    this.pendTask = pendTask;
    this.reqId = UUID.fromString(pendTask.getRequestId());
  }

  protected TaskRequest() {
  }

  public Task getTask() {
    return task;
  }

  protected void init(TaskScheduler scheduler, UUID reqId) {
    this.scheduler = scheduler;
    this.reqId = reqId;
    pendTask = new PendTask();
    pendTask.setRequestId(reqId.toString());
    pendTask.setId(UUID.randomUUID().toString());
    pendTask.setClassTask(task.getClass().getName());
  }

  protected void initInLocal() {
    DecadeDatabase db = DecadeDatabase.getInstance();
    dao = db.getPendDao();
    Bundle data = task.getData();

    List<PendInput> inputList = new ArrayList<>();
    for (String key : data.keySet()) {
      Object v = data.get(key);
      String className = v.getClass().getName();
      String value = null;
      if (v instanceof String) {
        value = (String) v;
      } else if (v instanceof Integer) {
        value = Integer.toString((Integer) v);
      } else if (v instanceof Long) {
        value = Long.toString((Long) v);
      }
      PendInput input = new PendInput();
      input.setClassName(className);
      input.setKey(key);
      input.setValue(value);
      inputList.add(input);
    }

    db.runInTransaction(() -> {
      dao.insert(pendTask);
      for (PendInput input : inputList) {
        input.setPendId(pendTask.getId());
      }
      dao.insertAllInput(inputList);
    });
  }

  /* TODO : In case the task is
   *   completed but the entity haven't deleted in local (when system suddenly destroy my app)
   *   The responsibility is of the logic communication between client and server */
  protected void execute() {
    task.onTaskPrepare(details);
    details.monitor.taskState.postValue("In progress");
    scheduler.onTaskInProgress(reqId, pendTask.getId());
    task.doTask();
    postExecuted();
  }

  protected void postExecuted() {
    task.onTaskCompleted();
    if (pendTask != null) {
      dao.delete(pendTask);
    }
    scheduler.onTaskComplete(reqId, pendTask.getId());
    details.monitor.taskState.postValue("Complete");
  }

  public static abstract class Builder {
    protected TaskDetails details;
    private Task task;

    public Builder() {
      this.details = new TaskDetails();
    }

    public <T extends Task> T fromTask(Class<T> taskClass) {
      Constructor<T> c = null;
      try {
        c = taskClass.getConstructor();
        return (T) (task = c.newInstance());
      } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
               InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }

    public Builder setData(Bundle data) {
      details.data = data;
      return this;
    }

    public Builder setWillRestore(boolean willRestore) {
      details.willRestore = willRestore;
      return this;
    }

    protected abstract <R extends TaskRequest> R doBuild();

    public <R extends TaskRequest> R build() {
      TaskRequest taskRequest = doBuild();
      taskRequest.task = task;
      taskRequest.details = details;
      return (R) taskRequest;
    }
  }
}
