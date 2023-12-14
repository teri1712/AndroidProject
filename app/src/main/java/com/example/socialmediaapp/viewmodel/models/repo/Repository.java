package com.example.socialmediaapp.viewmodel.models.repo;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.viewmodel.models.repo.callback.DataEmit;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class Repository<T> {

   private class Accumulator {
      private int offset;
      private int length;
      private int progress;
      private MutableLiveData<String> callBack;

      private Accumulator(int offset, int length, MutableLiveData<String> callBack) {
         isPolling = true;
         this.offset = offset;
         this.length = length;
         this.callBack = callBack;
         progress = 0;
      }

      private void pollItems() {
         int l = Math.min(length - progress, loadedItems.size() - (offset + progress));
         progress += l;
         if (l >= length || l == 0) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("offset", offset);
            data.put("length", progress);
            itemUpdate.setValue(new Update(Update.Op.ADD, data));
            consume(l);
            callBack.setValue("Success");

            isPolling = false;
            for (Task task : qTasks) {
               task.action.run();
            }
            qTasks.clear();
            return;
         }
         Task task = new Task();
         task.callBack = callBack;
         task.action = () -> {
            pollItems();
         };
         qTasks.add(0, task);
         fetchFromDataLayer();
      }
   }

   protected class Task {
      protected MutableLiveData<String> callBack;
      protected Runnable action;
   }

   protected interface DataEmitProcessor {
      void onResponse(DataEmit res);
   }

   private class FetchResponseProcessor implements DataEmitProcessor {
      @Override
      public void onResponse(DataEmit res) {
         String status = res.getStatus();
         if (status.equals("End") || status.equals("Renew")) {
            isFetching = false;
            for (Task task : qTasks) {
               task.callBack.setValue(status);
            }
            qTasks.clear();
            return;
         }
         String type = res.getType();
         if (type.equals("fetch response")) {
            isFetching = false;
            List<T> items = (List<T>) res.getData().get("items");
            loadedItems.addAll(items);
         }
      }
   }

   protected MutableLiveData<Update> itemUpdate;
   protected List<T> loadedItems;

   protected int countLoaded;
   // because the implementation of DataAccessHandler limits on the number of item fetched, polling technique used for a request that
   // need to load lot of items, ex : comment fragment will load entrance list about 10 items to fill up the screen
   // queue tasks is used for queueing other action to wait until polling item complete, because it may interrupt the polling operation
   protected ArrayList<Task> qTasks;
   protected boolean isFetching;
   protected boolean isPolling;

   protected DataAccessHandler<T> dataAccessHandler;
   protected List<DataEmitProcessor> dataEmitProcessors;

   private Disposable dataEmitDisposable;

   public Repository(DataAccessHandler<T> dataAccessHandler) {
      this.dataAccessHandler = dataAccessHandler;
      isFetching = false;
      countLoaded = 0;
      itemUpdate = new MutableLiveData<>();
      loadedItems = new ArrayList<>();
      dataEmitProcessors = new ArrayList<>();
      dataEmitProcessors.add(new FetchResponseProcessor());
      itemUpdate = new MutableLiveData<>();
      qTasks = new ArrayList<>();
      dataEmitDisposable = dataAccessHandler.getDataEmitter().observeOn(AndroidSchedulers.mainThread()).subscribe(next -> {
         for (DataEmitProcessor dataEmitProcessor : dataEmitProcessors) {
            dataEmitProcessor.onResponse(next);
         }
      });
   }

   public MutableLiveData<Update> getItemUpdate() {
      return itemUpdate;
   }

   public T get(int pos) {
      return loadedItems.get(pos);
   }

   public int length() {
      return countLoaded;
   }

   private void consume(int cnt) {
      countLoaded += cnt;
      doCache();
   }

   private void doCache() {
      if (loadedItems.size() < countLoaded + 5) {
         fetchFromDataLayer();
      }
   }

   private void fetchFromDataLayer() {
      if (isFetching) return;

      isFetching = true;
      dataAccessHandler.fetchNewItems(loadedItems.isEmpty() ? null : loadedItems.get(loadedItems.size() - 1));
   }

   public LiveData<String> loadNewItems(Integer length) {
      final MutableLiveData<String> callBack = new MutableLiveData<>();
      if (loadedItems.size() >= countLoaded + length) {
         consume(length);
         callBack.setValue("Success");
      } else {
         Accumulator accumulator = new Accumulator(countLoaded, length, callBack);
         accumulator.pollItems();
      }
      return callBack;
   }

   public LiveData<String> uploadNewItem(Bundle data) {
      MediatorLiveData<String> callBack = new MediatorLiveData();
      if (isFetching) {
         Task task = new Task();
         task.callBack = callBack;
         task.action = () -> doUpload(callBack, data);
         qTasks.add(task);
      } else {
         doUpload(callBack, data);
      }
      return callBack;
   }

   private void doUpload(MediatorLiveData<String> callBack, Bundle data) {
      callBack.addSource(dataAccessHandler.uploadNewItem(data), hashMap -> {
         String status = (String) hashMap.get("status");
         if (status.equals("Success")) {
            T item = (T) hashMap.get("item");
            loadedItems.add(0, item);
            countLoaded++;
            HashMap<String, Object> m = new HashMap<>();
            m.put("offset,", 0);
            m.put("item", item);
            itemUpdate.setValue(new Update(Update.Op.ADD, m));
         }
         callBack.setValue(status);
      });
   }

   public LiveData<String> recycle() {
      MediatorLiveData<String> callBack = new MediatorLiveData<>();
      callBack.addSource(dataAccessHandler.renew(loadedItems.get(countLoaded - 1)), s -> {
         loadedItems = loadedItems.subList(countLoaded, loadedItems.size());
         countLoaded = 0;
         callBack.setValue(s);
      });
      return callBack;
   }

   public void close() {
      for (Task task : qTasks) {
         task.callBack.setValue("Disposed");
      }
      dataEmitDisposable.dispose();
   }
}
