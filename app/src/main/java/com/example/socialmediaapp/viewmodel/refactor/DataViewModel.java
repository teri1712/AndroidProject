package com.example.socialmediaapp.viewmodel.refactor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel<T> extends ViewModel {
    protected MutableLiveData<T> liveData;

    public DataViewModel() {
    }

    public MutableLiveData<T> getLiveData() {
        return liveData;
    }
}
