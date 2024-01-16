package com.example.socialmediaapp.application.session;

import android.util.ArrayMap;

import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;

import java.util.Map;
import java.util.TreeMap;

/* Firebase Cloud Messaging for real-time update */
public class FcmAccessHandler<T, B> extends RealTimeAccessHandler<T, B> {
  public FcmAccessHandler(
          DataAccessHelper<T> accessHelper,
          DataUpdateHelper<T> updateHelper) {
    super(accessHelper, updateHelper);
  }

  public void onNewMessage(final B body) {
    ensureThread(() -> {
      Map<String, Object> data = new ArrayMap<>();
      data.put("item", body);
      updateNewItems(data);
    });
  }
}
