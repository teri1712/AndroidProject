package com.example.socialmediaapp.application.repo.core;

import android.os.Bundle;

import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.application.repo.core.tasks.FutureResult;
import com.example.socialmediaapp.application.repo.core.tasks.Task;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;
import com.example.socialmediaapp.application.session.UploadHelper;
import com.example.socialmediaapp.application.session.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadAdapter<T> {
  private final Class<? extends UploadTask<T>> uploadTask;
  private UploadHelper<T> uploadHelper;
  private Repository<T> repo;
  private Repository<T>.DataEmitProcessor emitProcessor;

  public UploadAdapter(Class<? extends UploadTask<T>> uploadTask) {
    this.uploadTask = uploadTask;
  }

  protected void init(Repository<T> repo) {
    this.repo = repo;
    this.uploadHelper = new UploadHelper<>(repo.handler, uploadTask);
  }

  protected void applyEmitListener(Repository<T>.DataEmitProcessor emitProcessor) {
    this.emitProcessor = emitProcessor;
    emitProcessor.addListener(new UploadResponseProcessor());
  }

  public LiveData<String> uploadNewItem(Bundle data) {
    Task<String> task = new Task<String>() {
      private FutureResult<String> doUpload() {
        FutureResult<String> future = new FutureResult<>();
        DataEmitListener listener = new DataEmitListener() {
          @Override
          public void onResponse(DataEmit res) {
            emitProcessor.removeListener(this);
            String type = res.getType();
            if (type.equals("upload")) {
              future.onComplete("Success");
            }
          }
        };
        emitProcessor.addListener(listener);
        uploadHelper.uploadNewItem(data);
        return future;
      }

      @Override
      protected FutureResult<String> onDoTask() {
        return doUpload();
      }
    };
    repo.scheduler.schedule(task);
    return task.getCallBack();
  }


  private class UploadResponseProcessor implements DataEmitListener {
    @Override
    public void onResponse(DataEmit res) {
      String type = res.getType();
      if (type.equals("upload")) {
        T item = (T) res.getData().get("item");
        List<T> l = new ArrayList<>();
        l.add(item);
        repo.updateNewItems(0, l);
      }
    }
  }
}
