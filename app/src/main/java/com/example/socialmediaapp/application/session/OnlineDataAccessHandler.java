package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.application.session.helper.DataAccessHelper;

public class OnlineDataAccessHandler<T> extends DataAccessHandler<T> {

    public OnlineDataAccessHandler(DataAccessHelper<T> dataAccessHelper) {
        super(dataAccessHelper);
    }

    private void notifyNewItems(Bundle bundle) {
        post(() -> {
            waitTillWorkerFinish();
            hintWorkerToFetch();
        });
    }
}
