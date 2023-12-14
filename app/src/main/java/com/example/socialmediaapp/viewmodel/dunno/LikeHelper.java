package com.example.socialmediaapp.viewmodel.dunno;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public interface LikeHelper {
    MutableLiveData<Boolean> getLikeSync();

    LiveData<String> doLike();

    LiveData<String> doUnLike();
}