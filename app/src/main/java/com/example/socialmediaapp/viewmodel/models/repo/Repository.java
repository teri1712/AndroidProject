package com.example.socialmediaapp.viewmodel.models.repo;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.viewmodel.models.repo.callback.FetchResponseProcessor;
import com.example.socialmediaapp.viewmodel.models.repo.callback.FetchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Repository<T> {
    public class UpdateRepositoryProcessor implements FetchResponseProcessor<T> {
        @Override
        public void onResponse(FetchResponse<List<T>> res) {
            if (!res.getStatus().equals("Success")) return;
            List<T> items = res.getData();
            if (items == null) return;
            loadedItems.addAll(items);
        }
    }

    private List<FetchResponseProcessor<T>> loadProcessors;
    protected List<T> loadedItems;
    protected DataAccessHandler<T> dataAccessHandler;

    public Repository(DataAccessHandler<T> dataAccessHandler) {
        this.dataAccessHandler = dataAccessHandler;
        loadedItems = new ArrayList<>();
        loadProcessors = new ArrayList<>();
        loadProcessors.add(new UpdateRepositoryProcessor());
        dataAccessHandler.getDataEmitter().subscribe(next -> {
            for (FetchResponseProcessor<T> p : loadProcessors) p.onResponse(next);
        });
    }

    public MutableLiveData<List<T>> fetchNewItems(Bundle query) {
        final MutableLiveData<List<T>> callBack = new MutableLiveData<>();
        int countLoaded = query.getInt("count loaded");

        if (loadedItems.size() >= countLoaded + 8) {
            List<T> res = loadedItems.subList(countLoaded, countLoaded + 8);
            callBack.setValue(res);
            return callBack;
        }
        FetchResponseProcessor<T> nextPostsEventInvokeCallback = new FetchResponseProcessor<T>() {
            @Override
            public void onResponse(FetchResponse<List<T>> res) {
                if (!res.getStatus().equals("Success")) {
                    callBack.postValue(new ArrayList<>());
                    return;
                }
                List<T> l = loadedItems.subList(countLoaded, countLoaded + 8);
                callBack.setValue(l);
                loadProcessors.remove(this);
            }
        };

        loadProcessors.add(nextPostsEventInvokeCallback);
        //convert Bundle into Data
        Bundle dQuery = new Bundle();
        dQuery.putInt("count loaded", loadedItems.size());
        dataAccessHandler.fetchNewItems(dQuery);
        return callBack;
    }

    public LiveData<HashMap<String, Object>> uploadNewItem(Bundle data) {
        return dataAccessHandler.uploadNewItem(data);
    }
}
