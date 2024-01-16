package com.example.socialmediaapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimePingService extends Service {
   private MutableLiveData<Long> timeCount;
   private Binder binder = new CounterBinder();
   private ExecutorService executor = Executors.newSingleThreadExecutor();

   public class CounterBinder extends Binder {
      public TimePingService timePingService;

      public CounterBinder() {
         timePingService = TimePingService.this;
      }
   }

   @Override
   public void onCreate() {
      super.onCreate();
      timeCount = new MutableLiveData<>();
      executor.execute(() -> {
         while (true) {
            try {
               Thread.sleep(250 * 60);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            timeCount.postValue(System.currentTimeMillis());
         }
      });
   }

   @Override
   public void onDestroy() {
      executor.shutdown();
      super.onDestroy();
   }

   public MutableLiveData<Long> getTimeCount() {
      return timeCount;
   }

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return binder;
   }
}
