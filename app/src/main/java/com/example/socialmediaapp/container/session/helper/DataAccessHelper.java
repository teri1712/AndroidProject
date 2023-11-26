package com.example.socialmediaapp.container.session.helper;

import android.os.Bundle;

import androidx.work.Data;
import androidx.work.WorkInfo;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Future;

public abstract class DataAccessHelper<T> {


    public DataAccessHelper() {
    }

    public abstract List<T> tryToFetchFromLocalStorage(Data query);

    public abstract ListenableFuture<WorkInfo> fetchFromServer(Data query);

    public abstract ListenableFuture<WorkInfo> uploadToServer(Data query);

}
