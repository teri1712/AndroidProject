package com.example.socialmediaapp.application.repo.core;

import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.repo.core.tasks.FutureResult;
import com.example.socialmediaapp.application.repo.core.tasks.Task;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.repo.core.tasks.Scheduler;
import com.example.socialmediaapp.application.repo.core.tasks.SerialTaskScheduler;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;
import com.example.socialmediaapp.application.repo.core.utilities.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class Repository<T> {
  protected List<T> items;
  protected int countLoaded;
  protected DataEmitProcessor emitProcessor;
  protected Scheduler scheduler;
  protected DataAccessHandler<T> handler;
  protected UploadAdapter<T> uploadAdapter;
  private MutableLiveData<Update> itemUpdate;
  private Disposable dataEmitDisposable;
  private boolean isFetching;

  public Repository(DataAccessHandler<T> handler) {
    this.handler = handler;
    this.scheduler = new SerialTaskScheduler();
    this.itemUpdate = new MutableLiveData<>();
    this.items = new ArrayList<>();
    this.isFetching = false;
    this.countLoaded = 0;
    initEmitListener();
    dataEmitDisposable = handler.getUiEmitter()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(next -> emitProcessor.onResponse(next));
  }

  protected void initEmitListener() {
    emitProcessor = new DataEmitProcessor();
    emitProcessor.addListener(new FetchResponseProcessor());
    if (uploadAdapter != null) {
      uploadAdapter.applyEmitListener(emitProcessor);
    }
  }

  public void setUploadAdapter(UploadAdapter<T> uploadAdapter) {
    this.uploadAdapter = uploadAdapter;
    uploadAdapter.init(this);
    uploadAdapter.applyEmitListener(emitProcessor);
  }

  public LiveData<Update> getItemUpdate() {
    return itemUpdate;
  }

  public T get(int pos) {
    return items.get(pos);
  }

  public int length() {
    return countLoaded;
  }

  protected void consume(int cnt) {
    countLoaded += cnt;
    if (items.size() < countLoaded + 8) {
      // cache
      fetchFromDataLayer();
    }
  }

  protected void setUpdate(@NonNull Update update) {
    itemUpdate.setValue(update);
    itemUpdate.setValue(null);
  }

  protected void updateNewItems(int offset, List<T> l) {
    items.addAll(offset, l);
    Map<String, Object> m = new ArrayMap<>();
    m.put("offset", offset);
    m.put("length", l.size());
    countLoaded += l.size();
    setUpdate(new Update(Update.Op.ADD, m));
  }

  protected void fetchFromDataLayer() {
    if (isFetching) return;
    isFetching = true;
    handler.fetch(!items.isEmpty()
            ? items.get(items.size() - 1)
            : null);
  }

  public LiveData<String> fetchNewItem(int length) {
    if (items.size() >= countLoaded + length) {
      Map<String, Object> data = new ArrayMap<>();
      data.put("offset", countLoaded);
      data.put("length", length);
      consume(length);
      setUpdate(new Update(Update.Op.ADD, data));
      return new MutableLiveData<>("Success");
    }
    Task<String> task = new AccumulateTask(this, countLoaded, length);
    scheduler.schedule(task);
    return task.getCallBack();
  }

  public LiveData<String> renew() {
    Task<String> task = new Task<String>() {
      @Override
      protected FutureResult<String> onDoTask() {
        return handler.deleteUpTo(items.get(countLoaded - 1));
      }
    };
    scheduler.schedule(task);
    return task.getCallBack();
  }


  protected void onInterrupted(String status) {
    isFetching = false;
    scheduler.discardAll();
    initEmitListener();
  }

  protected void onRenew() {
    items = items.subList(countLoaded, items.size());
    countLoaded = 0;
    setUpdate(new Update(Update.Op.RECYCLE, null));
  }

  public void close() {
    countLoaded = 0;
    emitProcessor.onResponse(new DataEmit(null, "End", null));
    dataEmitDisposable.dispose();
  }

  protected class DataEmitProcessor extends AbstractDataEmitDelegate {
    @Override
    protected boolean willNotProcess(DataEmit res) {
      String status = res.getStatus();
      boolean willNotProcess = status.equals("End")
              || status.equals("Renew");
      if (willNotProcess) {
        onInterrupted(status);
      }
      if (status.equals("Renew")) {
        onRenew();
      }
      return willNotProcess;
    }
  }

  private class FetchResponseProcessor implements DataEmitListener {
    @Override
    public void onResponse(DataEmit res) {
      String type = res.getType();
      if (type.equals("fetch")) {
        isFetching = false;
        List<T> list = (List<T>) res.getData().get("items");
        items.addAll(list);
      }
    }
  }
}
