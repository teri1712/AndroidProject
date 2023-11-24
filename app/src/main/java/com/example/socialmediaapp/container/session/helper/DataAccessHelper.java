package com.example.socialmediaapp.container.session.helper;

import android.os.Bundle;

import androidx.work.Data;
import androidx.work.WorkInfo;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Future;

public abstract class DataAccessHelper<T> {


    private boolean needToWait;
    final private Object mutex = new Object();
    public DataAccessHelper() {
        needToWait = false;
    }

    public abstract List<T> tryToFetchFromLocalStorage(Data query);

    public abstract ListenableFuture<WorkInfo> fetchFromServer(Data query);

    public abstract ListenableFuture<WorkInfo> uploadToServer(Data query);

    public void checkForSync(String typeOfAction) {
        synchronized (mutex) {
            if (needToWait) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onDoingAction(String typeOfAction) {
        synchronized (mutex) {
            needToWait = true;
        }
    }

    public void onCompleteAction(String typeOfAction) {
        synchronized (mutex) {
            needToWait = false;
            mutex.notifyAll();
        }
    }

}
