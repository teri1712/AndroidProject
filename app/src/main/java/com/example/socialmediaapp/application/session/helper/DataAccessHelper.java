package com.example.socialmediaapp.application.session.helper;

import android.os.Bundle;

import androidx.work.Data;
import androidx.work.WorkInfo;

import com.example.socialmediaapp.application.session.SessionHandler;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public abstract class DataAccessHelper<T> {
    protected SessionHandler session;
    public abstract List<T> tryToFetchFromLocalStorage(Bundle query);

    public abstract Bundle fetchFromServer(Bundle query) throws IOException;

    public abstract T uploadToServer(Bundle query) throws IOException, FileNotFoundException;

    public abstract void clean();

    public void setSession(SessionHandler session) {
        this.session = session;
    }

}
