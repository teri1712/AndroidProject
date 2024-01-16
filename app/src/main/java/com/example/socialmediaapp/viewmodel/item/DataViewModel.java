package com.example.socialmediaapp.viewmodel.item;

import androidx.lifecycle.LiveData;
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
