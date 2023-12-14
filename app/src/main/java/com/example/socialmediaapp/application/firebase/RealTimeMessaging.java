package com.example.socialmediaapp.application.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.MessageHome;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.MessageSessionHandler;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class RealTimeMessaging extends FirebaseMessagingService {

   private OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;

   @Override
   public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
      MessageSessionHandler messageSessionHandler = onlineSessionHandler.getMessageSessionHandler();
      MessageSessionHandler.MessageResolver messageResolver = messageSessionHandler.getMessageInterceptor();


      Map<String, String> message = remoteMessage.getData();
      String type = message.get("type");
      String msgContent = null;
      Bundle msg = new Bundle();
      msg.putString("sender", message.get("sender"));
      msg.putInt("chat id", Integer.parseInt(message.get("chat id")));
      msg.putLong("time", Long.parseLong(message.get("time")));
      msg.putInt("ord", Integer.parseInt(message.get("ord")));
      msg.putString("type", type);
      if (type.equals("text")) {
         msgContent = message.get("content");
         msg.putString("content", message.get("content"));
      } else if (type.equals("image")) {
         msgContent = "has sent an image";
         msg.putInt("media id", Integer.parseInt(message.get("media id")));
      } else if (type.equals("icon")) {
         msgContent = "has sent an icon";
      }

      messageResolver.intercept(msg);

      Handler mainThread = new Handler(Looper.getMainLooper());

      final String x = msgContent;

      mainThread.post(() -> {
         boolean isOnForeground = messageSessionHandler.isOnForeground();

         if (!isOnForeground) {
            // Display a notification
            Intent notificationIntent = new Intent(RealTimeMessaging.this, MessageHome.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(RealTimeMessaging.this, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(RealTimeMessaging.this, "channel_id")
                    .setSmallIcon(R.drawable.inactive_bell_24)
                    .setContentTitle("New Message")
                    .setContentText(x)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
         }
      });
   }
}
