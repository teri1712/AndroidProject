package com.example.socialmediaapp.viewmodel.models.repo;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;

import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.viewmodel.models.repo.interceptor.FetchResponseProcessor;
import com.example.socialmediaapp.viewmodel.models.repo.interceptor.FetchResponse;

import java.util.ArrayList;
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
    private List<T> loadedItems;
    private DataAccessHandler<T> dataAccessHandler;

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

        if (loadedItems.size() > countLoaded + 5) {
            List<T> res = loadedItems.subList(countLoaded, countLoaded + 5);
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
                List<T> l = loadedItems.subList(countLoaded, countLoaded + 5);
                callBack.setValue(l);
                loadProcessors.remove(this);
            }
        };

        loadProcessors.add(nextPostsEventInvokeCallback);
        //convert Bundle into Data
        Data dQuery = new Data.Builder().putInt("count loaded", loadedItems.size()).build();
        dataAccessHandler.fetchNewItems(dQuery);
        return callBack;
    }

    // return id of uploaded item
    public MutableLiveData<Integer> uploadNewItem(Bundle data) {
        //convert to Data here
        Data dData = null;
        return dataAccessHandler.uploadNewItem(dData);
    }
}
