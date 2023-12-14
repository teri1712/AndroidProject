package com.example.socialmediaapp.application.session;


import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.helper.DataAccessHelper;
import com.example.socialmediaapp.viewmodel.models.repo.callback.DataEmit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class DataAccessHandler<T> extends SessionHandler {
    private PublishSubject<Bundle> networkDataEmitter;
    protected PublishSubject<DataEmit> dataEmitter;
    protected DataAccessHelper<T> dataAccessHelper;

    public DataAccessHandler(DataAccessHelper<T> dataAccessHelper) {
        super();
        this.dataAccessHelper = dataAccessHelper;
        dataEmitter = PublishSubject.create();
    }

    @Override
    protected void init() {
        super.init();
        dataAccessHelper.setSession(this);
    }

    public PublishSubject<DataEmit> getDataEmitter() {
        return dataEmitter;
    }

    protected void hintWorkerToFetch() {
        final PublishSubject<Bundle> emitter = networkDataEmitter;
        postToWorker(() -> {
            Bundle result = null;
            try {
                result = dataAccessHelper.loadFromServer();
                result.putString("status", "Success");
            } catch (IOException e) {
                result = new Bundle();
                result.putString("status", "Error");
                e.printStackTrace();
            }
            final Bundle fresult = result;
            post(() -> emitter.onNext(fresult));
        });
    }

    public void fetchNewItems(T lastItem) {
        post(() -> {
            HashMap<String, Object> query = new HashMap<>();
            query.put("last item", lastItem);
            query.put("length", 5);
            List<T> get = dataAccessHelper.loadFromLocalStorage(query);
            if (get.size() == 5) {
                HashMap<String, Object> m = new HashMap();
                m.put("items", get);
                dataEmitter.onNext(new DataEmit(m, "Success", "fetch response"));
                return;
            }
            hintWorkerToFetch();
            networkDataEmitter.take(1).subscribe(next -> {
                String status = next.getString("status");
                if (status.equals("Success")) {
                    int cntLoaded = get.size();
                    query.put("length", 5 - cntLoaded);
                    query.put("last item", cntLoaded == 0 ? lastItem : get.get(cntLoaded - 1));
                    get.addAll(dataAccessHelper.loadFromLocalStorage(query));
                }
                HashMap<String, Object> m = new HashMap();
                m.put("items", get);
                dataEmitter.onNext(new DataEmit(m, status, "fetch response"));
            });
        });
    }

    public MutableLiveData<HashMap<String, Object>> uploadNewItem(Bundle data) {
        final MutableLiveData<HashMap<String, Object>> callBack = new MutableLiveData<>();
        post(() -> {
            HashMap<String, Object> result = new HashMap<>();
            postToWorker(() -> {
                try {
                    T item = dataAccessHelper.uploadToServer(data);
                    result.put("status", "Success");
                    result.put("item", item);
                } catch (IOException e) {
                    result.put("status", "Failed");
                    e.printStackTrace();
                }
            });
            callBack.postValue(result);
        });
        return callBack;
    }

    public LiveData<String> renew(T lastItem) {
        MutableLiveData<String> res = new MutableLiveData<>();
        post(() -> {
            waitTillWorkerFinish();

            dataEmitter.onNext(new DataEmit(null, "Renew", null));

            networkDataEmitter.onComplete();
            networkDataEmitter = PublishSubject.create();
            sessionRegistry.clear();

            dataAccessHelper.popRead(lastItem);
            res.postValue("Success");
        });
        return res;
    }

    @Override
    protected void clean() {
        super.clean();
        dataAccessHelper.cleanAll();
    }
}
