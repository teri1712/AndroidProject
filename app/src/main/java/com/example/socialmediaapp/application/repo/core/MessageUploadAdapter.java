package com.example.socialmediaapp.application.repo.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.application.session.MessageUploadTask;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

public class MessageUploadAdapter extends UploadAdapter<MessageItemModel> {
  private Handler mainThread;

  public MessageUploadAdapter() {
    super(MessageUploadTask.class);
    mainThread = new Handler(Looper.getMainLooper());
  }

  private void doSend(MediatorLiveData<String> cb, Bundle data) {
    cb.addSource(uploadNewItem(data), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        cb.setValue(s);
      }
    });
  }

  public LiveData<String> sendMessage(Bundle data) {
    MediatorLiveData<String> callBack = new MediatorLiveData<>();
    /* NOTE : incompatible with recycler view */
    mainThread.post(() -> doSend(callBack, data));
    return callBack;
  }

}
