package com.example.socialmediaapp.models.messenger.chat;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

import com.example.socialmediaapp.application.DecadeApplication;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TextingUpdater {
  private DatabaseReference ref;
  private ScheduledExecutorService scheduledExecutor;
  private Handler mainThread;
  private int textTurn;
  private boolean cache;

  public TextingUpdater(DatabaseReference ref) {
    this.ref = ref;
    this.mainThread = new Handler(Looper.getMainLooper());
    this.scheduledExecutor = DecadeApplication.getInstance().sharedScheduledExecutor;
    this.textTurn = 0;
    this.cache = false;
  }

  @MainThread
  public void doTexting() {
    if (!cache) {
      ref.setValue(true);
      cache = true;
    }
    scheduledExecutor.schedule(new Runnable() {
      final int thisTaskTurn = ++textTurn;

      @Override
      public void run() {
        mainThread.post(() -> doStopTexting(thisTaskTurn));
      }
    }, 1000, TimeUnit.MILLISECONDS);
  }

  @MainThread
  private void doStopTexting(int thisTaskTurn) {
    if (textTurn == thisTaskTurn) {
      ref.setValue(false);
      cache = false;
    }
  }

  public void stopTexting() {
    ++textTurn;
    cache = false;
    ref.setValue(false);
  }
}
