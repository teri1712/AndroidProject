package com.example.socialmediaapp.application.network;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PendMonitor {
   protected MutableLiveData<String> taskState;
   private Bundle data;

   public PendMonitor(Bundle data) {
      taskState = new MutableLiveData<>();
   }

   public void setTaskState(MutableLiveData<String> taskState) {
      this.taskState = taskState;
   }

   public Bundle getData() {
      return data;
   }

   public void setData(Bundle data) {
      this.data = data;
   }

   public LiveData<String> getTaskState() {
      return taskState;
   }
}
