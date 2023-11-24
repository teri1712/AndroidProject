package com.example.socialmediaapp.viewmodels.refactor;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.container.session.SessionHandler;

public class DataViewModel<T> extends ViewModel {
    protected T data;
    public DataViewModel(SessionHandler.SessionRegistry sessionRegistry, T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
