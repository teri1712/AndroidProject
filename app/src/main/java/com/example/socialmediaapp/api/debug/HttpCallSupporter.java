package com.example.socialmediaapp.api.debug;

import com.example.socialmediaapp.application.AddCookieInterceptor;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.UpdateCookieInterceptor;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* Shorter call globally instead of accessing the retrofit from application context */
public class HttpCallSupporter {
  private static final Logger logger = Logger.getLogger(HttpCallSupporter.class.getName());
  private static Retrofit normal;
  /* For long polling */
  private static Retrofit forLongPolling;


  /* Called in the application context class */
  public static void init(
          DecadeApplication application,
          String localhost,
          OkHttpClient client) {
    normal = new Retrofit
            .Builder()
            .addConverterFactory(
                    GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .create()))
            .baseUrl(localhost)
            .client(client).build();
    OkHttpClient longPollingClient = new OkHttpClient.Builder()
            .addInterceptor(new AddCookieInterceptor())
            .addInterceptor(new UpdateCookieInterceptor())
            .followRedirects(false)
            .followSslRedirects(false)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build();
    forLongPolling = new Retrofit
            .Builder()
            .addConverterFactory(
                    GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .create()))
            .callbackExecutor(new Executor() {
              @Override
              public void execute(Runnable runnable) {
                application.mainHandler.post(runnable);
              }
            })
            .baseUrl(localhost)
            .client(longPollingClient).build();
  }

  public static <T> void debug(Response<T> res) {
    logger.log(Level.INFO, "Code " + res.code());
    assert res.code() == 200;
  }

  public static <T> T create(final Class<T> service) {
    return normal.create(service);
  }

  public static <T> T createLongPolling(final Class<T> service) {
    return forLongPolling.create(service);
  }


}
