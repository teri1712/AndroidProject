package com.example.socialmediaapp.application;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.security.crypto.EncryptedSharedPreferences;

import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.SequenceTable;
import com.example.socialmediaapp.application.network.TaskScheduler;
import com.example.socialmediaapp.application.session.CommentHandlerStore;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.PostHandlerStore;
import com.example.socialmediaapp.application.session.ReplyHandlerStore;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class DecadeApplication extends Application {
  public static final String localhost = "http://192.168.1.8:8080";
  public static final String DATA_LAYER_THREAD = "decade_1712";
  public Picasso picasso;
  public Set<String> cookies;
  public ScheduledExecutorService sharedScheduledExecutor;

  public OnlineSessionHandler onlineSessionHandler;
  public Handler mainHandler;
  public SharedPreferences sharedPreferences;

  @Override
  public void onCreate() {
    {
      CharSequence name = "Message";
      String description = "Message Notification";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel("decade_message", name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
    {
      CharSequence name = "Friend";
      String description = "Friend Notification";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel("decade_notification", name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
    {
      CharSequence name = "Post";
      String description = "Post Notification";
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel = new NotificationChannel("decade_post", name, importance);
      channel.setDescription(description);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
    try {
      sharedPreferences = EncryptedSharedPreferences.create(
              "decade",
              "Masahiro",
              this,
              EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
              EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      );
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.clear();
      editor.commit();
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    application = this;
    application.init();
    super.onCreate();
  }

  private void initHandlers() {
    /* main data layer thread, calling Api from UI layer always through this first */
    HandlerThread handlerThread = new HandlerThread(DATA_LAYER_THREAD);
    handlerThread.start();
    mainHandler = new Handler(handlerThread.getLooper());
    /* time out scheduler */
    sharedScheduledExecutor = Executors.newScheduledThreadPool(2);
    /* crucial loading class for the singleton appear */
    mainHandler.post(() -> {
      try {
        loadClasses();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    });
  }

  private void loadClasses() throws ClassNotFoundException {
    /* Thread pool handling network tasks */
    Class.forName(TaskScheduler.class.getName());
    Class.forName(MessageMonitorStore.class.getName());

    /* Handler for every item */
    Class.forName(PostHandlerStore.class.getName());
    Class.forName(CommentHandlerStore.class.getName());
    Class.forName(ReplyHandlerStore.class.getName());
  }

  private void init() {
    initHandlers();
    //for debugging
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    initDatabase();
    initHttpClient();
    initOnlineSession();
  }

  private void initDatabase() {
    mainHandler.post(() -> {
      DecadeDatabase.initDecadeDatabase(DecadeApplication.this);
      SequenceDao sequenceDao = DecadeDatabase.getInstance().getSequenceDao();
      SequenceTable sequenceTable = new SequenceTable();
      sequenceTable.setHead(0);
      sequenceTable.setTail(0);
      sequenceDao.insert(sequenceTable);
    });
  }

  private void initHttpClient() {
    cookies = new HashSet<>();
    File imgCacheDir = new File(getCacheDir(), "images");
    imgCacheDir.mkdir();
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AddCookieInterceptor())
            .addInterceptor(new UpdateCookieInterceptor())
            .followRedirects(false)
            .followSslRedirects(false)
            .cache(new Cache(imgCacheDir, 400_000_000))
            .build();
    initPicasso(client);
    HttpCallSupporter.init(this, localhost, client);
  }

  private void initPicasso(OkHttpClient client) {
    picasso = new Picasso.Builder(this)
            .downloader(new OkHttp3Downloader(client))
            .memoryCache(new LruCache(20_000_000))
            .build();
  }

  private void initOnlineSession() {
    onlineSessionHandler = new OnlineSessionHandler();
//      testing stuffs

//      FirebaseDatabase.getInstance()
//              .getReference()
//              .removeValue();
    mainHandler.post(() -> {
      onlineSessionHandler.initAndValidatePrincipal();
      OnlineSessionHandler.create(onlineSessionHandler);
    });
  }

  static private DecadeApplication application;

  static public DecadeApplication getInstance() {
    return application;
  }
}
