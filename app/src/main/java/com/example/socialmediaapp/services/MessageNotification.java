package com.example.socialmediaapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MessageNotification extends Service {

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      return super.onStartCommand(intent, flags, startId);
   }

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }
}
