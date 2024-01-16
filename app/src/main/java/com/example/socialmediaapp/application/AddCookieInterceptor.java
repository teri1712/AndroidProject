package com.example.socialmediaapp.application;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookieInterceptor implements Interceptor {
  @NonNull
  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    Request req = chain.request();

    Request.Builder builder = req.newBuilder();
    Set<String> cookies = DecadeApplication.getInstance().cookies;
    synchronized (cookies) {
      for (String c : cookies) builder.addHeader("Cookie", c);
    }
    return chain.proceed(builder.build());
  }
}
