package com.example.socialmediaapp.application.session;

import android.os.Bundle;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.TaskDetails;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 2 available real-time implementation :
 *  1. Long Polling
 *  2. Firebase Cloud Messaging  */
public abstract class RealTimeAccessHandler<T, B> extends DataAccessHandler<T> {
  protected final DataUpdateHelper<T> updateHelper;

  public RealTimeAccessHandler(
          DataAccessHelper<T> accessHelper,
          DataUpdateHelper<T> updateHelper) {
    super(accessHelper);
    this.updateHelper = updateHelper;
  }

  protected void updateNewItems(Map<String, Object> data) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    UpdateTask task = builder.fromTask(UpdateTask.class);
    task.setHandler(this);
    task.setPayload(data);

    SerialTaskRequest request = builder
            .setAlias(dataAccessHelper.getUuid())
            .setWillRestore(false)
            .build();

    postTask(request);
  }

  protected void postUpdateProcessed(Map<String, Object> data) {
  }

  protected void onUpdateCompleted(Map<String, Object> data) {
    uiEmitter.onNext(new DataEmit(data, "Success", "update"));
  }

  public void closeSession() {
  }

  public static class UpdateTask extends SessionTask {
    private RealTimeAccessHandler<?, ?> accessHandler;
    private DataUpdateHelper<?> updateHelper;
    private Map<String, Object> payload;

    public UpdateTask() {
      super();
    }

    @Override
    public void setHandler(SessionHandler handler) {
      super.setHandler(handler);
      accessHandler = (RealTimeAccessHandler<?, ?>) handler;
      updateHelper = accessHandler.updateHelper;
    }

    public void setPayload(Map<String, Object> payload) {
      this.payload = payload;
    }

    @Override
    public void doTask() {
      try {
        updateHelper.update(payload);
        accessHandler.postUpdateProcessed(payload);
        handler.post(() -> {
          accessHandler.onUpdateCompleted(payload);
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
