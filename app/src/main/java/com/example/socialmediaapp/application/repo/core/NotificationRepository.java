package com.example.socialmediaapp.application.repo.core;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.session.NotificationAccessHandler;
import com.example.socialmediaapp.models.NotificationModel;
import com.example.socialmediaapp.models.NotifyDetailsModel;

public class NotificationRepository extends RealTimeRepository<NotificationModel> {
  private MutableLiveData<Integer> cntUnRead;
  private NotificationAccessHandler accessHandler;

  public NotificationRepository(NotificationAccessHandler accessHandler) {
    super(accessHandler);
    this.accessHandler = accessHandler;
    LiveData<NotifyDetailsModel> notifyDetails = accessHandler.getNotifyDetails();
    cntUnRead = (MutableLiveData<Integer>) Transformations.map(notifyDetails, input -> input.getCountUnRead());
  }
  public MutableLiveData<Integer> getCntUnRead() {
    return cntUnRead;
  }
  public void consume() {
    accessHandler.doRead();
  }
}
