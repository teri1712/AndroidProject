package com.example.socialmediaapp.application.session;


import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.helper.DataAccessHelper;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.callback.FetchResponse;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class DataAccessHandler<T> extends SessionHandler {
    private PublishSubject<Bundle> networkDataEmitter;
    private PublishSubject<FetchResponse<List<T>>> dataEmitter;
    protected DataAccessHelper<T> dataAccessHelper;
    private int countLoadedInLocal;
    private Object mutex;
    private boolean isRunning;
    private Executor worker;

    public DataAccessHandler(Class<T> t, DataAccessHelper<T> dataAccessHelper) {
        super();
        worker = ApplicationContainer.getInstance().workers.get(t.getSimpleName());
        this.dataAccessHelper = dataAccessHelper;
        networkDataEmitter = PublishSubject.create();
        dataEmitter = PublishSubject.create();
        dataEmitter.observeOn(AndroidSchedulers.mainThread());
        countLoadedInLocal = 0;
        networkDataEmitter.subscribe(out -> {
            if (out.getString("status") == "error") return;

            countLoadedInLocal += out.getInt("count loaded", 0);
        });
        mutex = new Object();
        isRunning = false;
    }

    public PublishSubject<FetchResponse<List<T>>> getDataEmitter() {
        return dataEmitter;
    }

    private void hintWorkerToFetch(Bundle query) {
        postToWorker(() -> {
            Bundle result = null;
            try {
                result = dataAccessHelper.fetchFromServer(query);
                result.putString("status", "Success");
            } catch (IOException e) {
                result = new Bundle();
                result.putString("status", "error");
                e.printStackTrace();
            }
            final Bundle fresult = result;
            post(new Runnable() {
                @Override
                public void run() {
                    networkDataEmitter.onNext(fresult);
                }
            });
        });
    }

    private void localStorageCacheStrategy(Bundle input) {
        int countLoaded = input.getInt("count loaded");
        boolean doFetch = countLoaded + 10 > countLoadedInLocal;
        if (doFetch) {
            hintWorkerToFetch(input);
        }
    }

    public void fetchNewItems(final Bundle query) {
        post(() -> {
            Bundle input = new Bundle();
            input.putInt("count loaded", query.getInt("count loaded", 0));
            input.putInt("read", query.getInt("read", 0));
            List<T> get = dataAccessHelper.tryToFetchFromLocalStorage(query);
            if (get.size() < 8) {
                networkDataEmitter.take(1).subscribe(next -> {
                    String status = next.getString("status");
                    List<T> data = status.equals("Success") ? dataAccessHelper.tryToFetchFromLocalStorage(query) : null;
                    dataEmitter.onNext(new FetchResponse<>(data, status));
                });
                input.putString("command", "quicker");
            } else {
                dataEmitter.onNext(new FetchResponse<>(get, "Success"));
            }
            localStorageCacheStrategy(input);
        });
    }

    @Override
    protected void init() {
        dataAccessHelper.setSession(this);
        super.init();
    }

    @Override
    protected void clean() {
        super.clean();
        synchronized (mutex) {
            if (isRunning) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isInvalidated = true;
        }
        worker.execute(() -> dataAccessHelper.clean());
    }

    public MutableLiveData<HashMap<String, Object>> uploadNewItem(Bundle data) {
        final MutableLiveData<HashMap<String, Object>> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            HashMap<String, Object> result = new HashMap<>();
            try {
                T item = dataAccessHelper.uploadToServer(data);
                result.put("status", "Success");
                result.put("item", item);
            } catch (IOException e) {
                result.put("status", "Failed");
                e.printStackTrace();
            }
            final HashMap<String, Object> fresult = result;
            post(() -> callBack.postValue(fresult));
        }));
        return callBack;
    }

    protected void postToWorker(Runnable runnable) {
        Runnable frunnable = () -> {
            synchronized (mutex) {
                if (isInvalidated) {
                    return;
                }
                isRunning = true;
            }
            runnable.run();
            synchronized (mutex) {
                isRunning = false;
                mutex.notify();
            }

        };
        worker.execute(frunnable);
    }
}
