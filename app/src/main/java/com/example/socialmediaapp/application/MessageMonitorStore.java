package com.example.socialmediaapp.application;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.application.network.PendMonitor;

import java.util.TreeMap;

public class MessageMonitorStore {
  private static MessageMonitorStore store;

  static {
    store = new MessageMonitorStore();
  }

  public static MessageMonitorStore getInstance() {
    return store;
  }

  private final TreeMap<Integer, MediatorLiveData<String>> m;
  private final Handler mainThread;

  public MessageMonitorStore() {
    m = new TreeMap<>();
    mainThread = new Handler(Looper.getMainLooper());
  }

  public void create(Integer msgId) {
    mainThread.post(new Runnable() {
      @Override
      public void run() {
        m.put(msgId, new MediatorLiveData<>());
      }
    });
  }

  public void bind(Integer msgId, PendMonitor monitor) {
    mainThread.post(new Runnable() {
      @Override
      public void run() {
        MediatorLiveData<String> liveData = m.get(msgId);
        assert liveData != null;
        liveData.addSource(monitor.getTaskState(), new Observer<String>() {
          @Override
          public void onChanged(String s) {
            liveData.setValue(s);
          }
        });
      }
    });
  }

  public LiveData<String> findMessageStateLiveData(Integer msgId) {
    return m.get(msgId);
  }

  public void onCompleteSendMessage(Integer msgId) {
    mainThread.post(() -> {
      m.remove(msgId);
    });
  }
}
