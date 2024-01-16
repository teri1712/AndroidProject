package com.example.socialmediaapp.application.session;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.FriendApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.network.Scheduler;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.application.network.TaskScheduler;
import com.google.android.material.badge.BadgeUtils;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SessionHandler {
  protected final Object lock;
  protected int countEnqueued;
  protected boolean invalidated;
  private final MutableLiveData<String> sessionState;
  private final Handler handler;
  private final Scheduler scheduler;

  protected SessionHandler() {
    this.lock = new Object();
    this.sessionState = new MutableLiveData<>();
    this.handler = DecadeApplication.getInstance().mainHandler;
    this.scheduler = TaskScheduler.getInstance();
    invalidated = false;
    countEnqueued = 0;
  }

  public MutableLiveData<String> getSessionState() {
    return sessionState;
  }

  protected void init() {

  }

  protected void sync() {
  }

  protected void clean() {
  }

  protected void interrupt() {
    sessionState.postValue("interrupted");
  }

  protected void resume() {
    sessionState.postValue("resume");
  }

  protected void waitTillTaskFinish() {
    synchronized (lock) {
      while (countEnqueued != 0) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  protected void ensureThread(Runnable r) {
    String thName = Thread.currentThread().getName();
    if (Objects.equals(thName, DecadeApplication.DATA_LAYER_THREAD)) {
      r.run();
    } else {
      post(() -> r.run());
    }
  }

  public void finish() {
    ensureThread(this::invalidate);
  }

  protected void invalidate() {
    invalidated = true;
    waitTillTaskFinish();
    sessionState.postValue("invalidated");
    clean();
  }

  protected void postTask(TaskRequest request) {
    Runnable wrap = () -> {
      Task task = request.getTask();
      if (task instanceof SessionTask) {
        synchronized (lock) {
          countEnqueued++;
        }
      }
      scheduler.enqueue(request);
    };
    ensureThread(wrap);
  }

  protected void post(Runnable action) {
    handler.post(() -> {
      if (invalidated) return;
      action.run();
    });
  }
  public static class ActionHandleTask extends SessionTask {
    private Runnable action;

    public ActionHandleTask() {
    }

    public void setAction(Runnable action) {
      this.action = action;
    }

    @Override
    public void doTask() {
      action.run();
    }
  }
}
