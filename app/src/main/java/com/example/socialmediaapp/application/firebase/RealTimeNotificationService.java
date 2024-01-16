package com.example.socialmediaapp.application.firebase;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.api.entities.NotificationBody;
import com.example.socialmediaapp.application.session.MessageResolver;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

public class RealTimeNotificationService extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    Map<String, String> data = remoteMessage.getData();
    String action = data.get("action");
    Gson gson = new Gson();
    OnlineSessionHandler onlineHandler = OnlineSessionHandler.getInstance();
    switch (action) {
      case "message": {
        MessageResolver messageResolver = onlineHandler.getMsgResolver();
        MessageItemBody body = gson.fromJson(data.get("payload"), MessageItemBody.class);
        messageResolver.intercept(body);
        break;
      }
      case "notification": {
        NotificationBody body = gson.fromJson(data.get("payload"), NotificationBody.class);
        onlineHandler.onNewNotification(body);
        break;
      }
      default:
        assert false;
    }
  }
}
