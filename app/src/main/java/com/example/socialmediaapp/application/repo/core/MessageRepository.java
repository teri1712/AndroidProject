package com.example.socialmediaapp.application.repo.core;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.application.session.MessageAccessHandler;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

public class MessageRepository extends RealTimeRepository<MessageItemModel> {
  public MessageRepository(MessageAccessHandler dataAccessHandler) {
    super(dataAccessHandler);
  }
}
