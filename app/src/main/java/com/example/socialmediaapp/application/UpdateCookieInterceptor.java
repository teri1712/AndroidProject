package com.example.socialmediaapp.application;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.application.session.OnlineSessionHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Response;

public class UpdateCookieInterceptor implements Interceptor {
   @NonNull
   @Override
   public Response intercept(@NonNull Chain chain) throws IOException {
      Response res = chain.proceed(chain.request());
      Set<String> cookies = DecadeApplication.getInstance().cookies;
      List<String> setCookies = res.headers("Set-Cookie");
      if (!setCookies.isEmpty()) {
         synchronized (cookies) {
            for (String c : setCookies) {
               cookies.add(c);
               if (c.startsWith("remember-me")) {
                  OnlineSessionHandler onlineSessionHandler = DecadeApplication
                          .getInstance()
                          .onlineSessionHandler;
                  onlineSessionHandler.onRememberMeTokenChanged(c);
               }
            }
         }
      }
      return res;
   }
}
