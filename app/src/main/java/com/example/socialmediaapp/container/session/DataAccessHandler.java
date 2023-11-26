package com.example.socialmediaapp.container.session;


import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.session.helper.DataAccessHelper;
import com.example.socialmediaapp.viewmodel.models.repo.interceptor.FetchResponse;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class DataAccessHandler<T> extends SessionHandler {
    private PublishSubject<Data> networkDataEmitter;
    private PublishSubject<FetchResponse<List<T>>> dataEmitter;
    private DataAccessHelper<T> dataAccessHelper;
    private int countLoadedInLocal;
    private String currentAction;
    private Runnable waiting;

    public DataAccessHandler(DataAccessHelper<T> dataAccessHelper) {
        super();
        this.dataAccessHelper = dataAccessHelper;
        networkDataEmitter = PublishSubject.create();
        dataEmitter = PublishSubject.create();
        dataEmitter.observeOn(AndroidSchedulers.mainThread());
        countLoadedInLocal = 0;
        networkDataEmitter.subscribe(out -> {
            countLoadedInLocal += out.getInt("count loaded", 0);
        });
    }

    public PublishSubject<FetchResponse<List<T>>> getDataEmitter() {
        return dataEmitter;
    }

    private void hintWorkerToFetch(Bundle query) {
        if (currentAction.equals("fetch")) {
            return;
        }
        currentAction = "fetch";
        Data input = new Data.Builder().putInt("read", query.getInt("read")).build();
        ListenableFuture<WorkInfo> future = dataAccessHelper.fetchFromServer(input);
        addFutureCallBack(future, new Runnable() {
            @Override
            public void run() {
                WorkInfo workInfo = null;
                try {
                    workInfo = future.get();
                    networkDataEmitter.onNext(workInfo.getOutputData());
                } catch (Exception e) {
                    e.printStackTrace();
                    Data error = new Data.Builder().putString("status", "error").build();
                    networkDataEmitter.onNext(error);
                }
                currentAction = "free";
                if (waiting != null) waiting.run();
                waiting = null;
            }
        });
    }

    private void localStorageCacheStrategy(Bundle input) {
        int countLoaded = input.getInt("count loaded");
        boolean doFetch = countLoaded + 10 > countLoadedInLocal;
        if (doFetch) {
            hintWorkerToFetch(input);
        }
    }

    public void fetchNewItems(final Data query) {
        post(new Runnable() {
            @Override
            public void run() {
                if (currentAction.equals("upload")) {
                    waiting = this;
                    return;
                }
                Bundle input = new Bundle();
                input.putInt("count loaded", query.getInt("count loaded", 0));
                input.putInt("read", query.getInt("read", 0));
                List<T> get = dataAccessHelper.tryToFetchFromLocalStorage(query);
                if (get.size() < 5) {
                    networkDataEmitter.take(1).subscribe(next -> {
                        String status = next.getString("Status");
                        List<T> data = status.equals("Success") ? dataAccessHelper.tryToFetchFromLocalStorage(query) : null;
                        dataEmitter.onNext(new FetchResponse<>(data, status));
                    });
                    input.putString("command", "quicker");
                } else {
                    dataEmitter.onNext(new FetchResponse<>(get, "Success"));
                }
                localStorageCacheStrategy(input);
            }
        });
    }

    @Override
    protected void clean() {
        super.clean();
    }

    public MutableLiveData<Integer> uploadNewItem(Data data) {
        final MutableLiveData<Integer> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                if (currentAction.equals("fetch")) {
                    waiting = this;
                    return;
                }
                currentAction = "upload";
                ListenableFuture<WorkInfo> future = dataAccessHelper.uploadToServer(data);
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            callBack.postValue(out.getInt("Id", -1));
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.postValue(-1);
                        }
                        currentAction = "free";
                        if (waiting != null) waiting.run();
                        waiting = null;
                    }
                });
            }
        });
        return callBack;
    }
}
