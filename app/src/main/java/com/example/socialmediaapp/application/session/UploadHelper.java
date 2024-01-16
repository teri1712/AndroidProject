package com.example.socialmediaapp.application.session;

import android.os.Bundle;
import android.util.ArrayMap;

import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

import java.util.Map;

public class UploadHelper<T> {
  private final Class<? extends UploadTask<T>> uploadTask;
  protected final DataAccessHandler<T> handler;

  public UploadHelper(DataAccessHandler<T> accessHandler,
                      Class<? extends UploadTask<T>> uploadTask) {
    this.uploadTask = uploadTask;
    this.handler = accessHandler;
  }

  public void uploadNewItem(Bundle data) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    UploadTask<T> task = builder.fromTask(uploadTask);
    task.setHandler(handler);
    task.setUploadHelper(this);
    TaskRequest request = builder
            .setAlias(handler.dataAccessHelper.getUuid())
            .setWillRestore(true)
            .setData(data)
            .build();
    handler.postTask(request);
  }

  protected void onItemUploaded(String status, T item) {
    handler.ensureThread(() -> {
      Map<String, Object> map = new ArrayMap<>();
      map.put("item", item);
      handler.uiEmitter.onNext(new DataEmit(map, status, "upload"));
    });
  }
}
