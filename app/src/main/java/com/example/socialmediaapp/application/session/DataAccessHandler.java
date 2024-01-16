package com.example.socialmediaapp.application.session;


import android.os.Bundle;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.TaskDetails;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.application.repo.core.tasks.FutureResult;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

/* TODO : For delete operations, better to make it transient */
public class DataAccessHandler<T> extends SessionHandler {
  private final Class<? extends SessionTask> downloadTask;
  protected PublishSubject<Bundle> networkEmitter;
  protected PublishSubject<DataEmit> uiEmitter;
  protected DataAccessHelper<T> dataAccessHelper;

  public DataAccessHandler(DataAccessHelper<T> accessHelper) {
    super();
    this.downloadTask = DownloadTask.class;
    this.dataAccessHelper = accessHelper;
    this.uiEmitter = PublishSubject.create();
    this.networkEmitter = PublishSubject.create();
    init();
  }

  protected TaskRequest createSerialTask(Runnable action) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(action);
    TaskRequest request = builder
            .setAlias(dataAccessHelper.getUuid())
            .setWillRestore(false)
            .build();
    return request;
  }

  public PublishSubject<DataEmit> getUiEmitter() {
    return uiEmitter;
  }

  protected void hintWorkerToFetch() {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    SessionTask task = builder.fromTask(downloadTask);
    task.setHandler(this);
    TaskRequest request = builder
            .setAlias(dataAccessHelper.getUuid())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  public void fetch(T lastItem) {
    post(() -> {
      Map<String, Object> query = new ArrayMap<>();
      query.put("last item", lastItem);
      query.put("length", 5);
      List<T> get = dataAccessHelper.loadFromLocal(query);

      if (get.size() == 5) {
        HashMap<String, Object> m = new HashMap();
        m.put("items", get);
        uiEmitter.onNext(new DataEmit(m, "Success", "fetch"));
        return;
      }
      hintWorkerToFetch();
      Disposable d = networkEmitter.take(1).subscribe(next -> {
        String status = next.getString("status");
        if (status.equals("Success")) {
          int cntLoaded = get.size();
          query.put("length", 5 - cntLoaded);
          query.put("last item", cntLoaded == 0 ? lastItem : get.get(cntLoaded - 1));
          get.addAll(dataAccessHelper.loadFromLocal(query));
        }
        Map<String, Object> m = new ArrayMap<>();
        m.put("items", get);
        uiEmitter.onNext(new DataEmit(m, status, "fetch"));
      });
    });
  }

  public FutureResult<String> deleteUpTo(T lastItem) {
    FutureResult<String> future = new FutureResult<>();
    post(() -> {
      waitTillTaskFinish();

      uiEmitter.onNext(new DataEmit(null, "Renew", null));

      networkEmitter.onComplete();
      networkEmitter = PublishSubject.create();

      dataAccessHelper.pop(lastItem);
      future.postComplete("Success");
    });
    return future;
  }

  @Override
  protected void clean() {
    dataAccessHelper.cleanAll();
  }

  public static class DownloadTask extends SessionTask {
    private DataAccessHandler<?> accessHandler;

    private DataAccessHelper<?> accessHelper;

    public DownloadTask() {
      super();
    }

    @Override
    public void setHandler(SessionHandler handler) {
      super.setHandler(handler);
      accessHandler = (DataAccessHandler<?>) handler;
      accessHelper = accessHandler.dataAccessHelper;
    }

    @Override
    public void doTask() {
      Bundle result;
      try {
        result = accessHelper.loadFromServer();
        result.putString("status", "Success");
      } catch (IOException e) {
        result = new Bundle();
        result.putString("status", "Error");
        e.printStackTrace();
      }
      final Bundle fResult = result;
      handler.post(new Runnable() {
        @Override
        public void run() {
          accessHandler.networkEmitter.onNext(fResult);
        }
      });
    }
  }
}
