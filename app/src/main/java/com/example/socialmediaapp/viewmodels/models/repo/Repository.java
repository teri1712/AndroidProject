package com.example.socialmediaapp.viewmodels.models.repo;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;

import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.viewmodels.refactor.DataViewModel;
import com.example.socialmediaapp.viewmodels.factory.DataViewModelFactory;
import com.example.socialmediaapp.viewmodels.models.repo.interceptor.FetchResponseProcessor;
import com.example.socialmediaapp.viewmodels.models.repo.interceptor.FetchResponse;

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

        //lookup main-memory cache first
        if (loadedItems.size() > 100) {
            List<T> res = new ArrayList<>();
            callBack.setValue(res);
            return callBack;
        }
        FetchResponseProcessor<T> nextPostsEventInvokeCallback = new FetchResponseProcessor<T>() {
            @Override
            public void onResponse(FetchResponse<List<T>> res) {
                List<T> l = new ArrayList<>();
                callBack.setValue(l);
                loadProcessors.remove(this);
            }
        };

        loadProcessors.add(nextPostsEventInvokeCallback);
        //convert Bundle into Data
        Data dQuery = null;
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
