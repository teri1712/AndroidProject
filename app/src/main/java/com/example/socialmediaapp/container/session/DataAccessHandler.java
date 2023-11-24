package com.example.socialmediaapp.container.session;


import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.session.helper.DataAccessHelper;
import com.example.socialmediaapp.viewmodels.models.repo.interceptor.FetchResponse;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class DataAccessHandler<T> extends SessionHandler {
    private PublishSubject<Data> networkDataEmitter;
    private PublishSubject<FetchResponse<List<T>>> dataEmitter;
    protected Executor dummyExecutor = ApplicationContainer.getInstance().dummyExecutor;
    private DataAccessHelper<T> dataAccessHelper;

    public DataAccessHandler(DataAccessHelper<T> dataAccessHelper) {
        super();
        this.dataAccessHelper = dataAccessHelper;
        networkDataEmitter = PublishSubject.create();
        dataEmitter = PublishSubject.create();
        dataEmitter.observeOn(AndroidSchedulers.mainThread());
    }

    public PublishSubject<FetchResponse<List<T>>> getDataEmitter() {
        return dataEmitter;
    }

    private void hintWorkerToFetch(Bundle session) {
        dataAccessHelper.checkForSync("fetch from server");
        dataAccessHelper.onDoingAction("fetch from server");
        Data query = null;

        ListenableFuture<WorkInfo> future = dataAccessHelper.fetchFromServer(query);
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
            }
        });

        future.addListener(new Runnable() {
            @Override
            public void run() {
                dataAccessHelper.onCompleteAction("fetch from server");
            }
        }, dummyExecutor);
    }

    private void localStorageCacheStrategy(Bundle session) {
        boolean doFetch = false;
        if (doFetch) {
            hintWorkerToFetch(session);
        }
    }

    public void fetchNewItems(final Data query) {
        post(new Runnable() {
            @Override
            public void run() {
                dataAccessHelper.checkForSync("fetch");
                Bundle session = new Bundle();
                List<T> get = dataAccessHelper.tryToFetchFromLocalStorage(query);
                if (get == null) {
                    networkDataEmitter.take(1).subscribe(next -> {
                        String status = next.getString("Status");
                        List<T> data = status.equals("Success") ? dataAccessHelper.tryToFetchFromLocalStorage(query) : null;
                        dataEmitter.onNext(new FetchResponse<>(data, status));
                    });
                    session.putString("command", "quicker");
                } else {
                    dataEmitter.onNext(new FetchResponse<>(get, "Success"));
                }
                localStorageCacheStrategy(session);
            }
        });
    }

    @Override
    protected void clean() {
        dataAccessHelper.checkForSync("invalidate");
        networkDataEmitter = PublishSubject.create();
        dataEmitter = PublishSubject.create();
        super.clean();
    }

    public MutableLiveData<Integer> uploadNewItem(Data data) {
        final MutableLiveData<Integer> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                dataAccessHelper.checkForSync("upload");
                dataAccessHelper.onDoingAction("upload");
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
                    }
                });

                future.addListener(new Runnable() {
                    @Override
                    public void run() {
                        dataAccessHelper.onCompleteAction("upload");
                    }
                }, dummyExecutor);
            }
        });
        return callBack;
    }
}
