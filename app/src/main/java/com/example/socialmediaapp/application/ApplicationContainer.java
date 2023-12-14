package com.example.socialmediaapp.application;


import android.app.Application;

import androidx.room.Room;

import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.SequenceTable;
import com.example.socialmediaapp.application.session.MessageSessionHandler;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationContainer extends Application {
   //use 10.0.2.2 to access localhost if you use emulator.
   public final String localhost = "http://192.168.0.103:8080";
   public Retrofit retrofit;
   public Set<String> cookies;
   public AppDatabase database;
   public ArrayList<ExecutorService> workers;
   public OnlineSessionHandler onlineSessionHandler;
   public Executor dataLayerExecutor = Executors.newSingleThreadExecutor();

   public SessionHandler.SessionRepository sessionRepository;
   public FirebaseDatabase firebaseDatabase;

   @Override
   public void onCreate() {
      super.onCreate();
      application = this;
      application.init();
   }

   private void init() {
      firebaseDatabase = FirebaseDatabase.getInstance();
      workers = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
         workers.add(Executors.newSingleThreadExecutor());
      }
      //for debugging
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      initDatabase();
      initHttpClient();

      initOnlineSession();

   }

   private void initDatabase() {
      database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "MyApp").fallbackToDestructiveMigration().build();

      dataLayerExecutor.execute(() -> {
         SequenceDao sequenceDao = database.getSequenceDao();

         SequenceTable sequenceTable = new SequenceTable();
         sequenceTable.setHead(0);
         sequenceTable.setTail(0);
         sequenceDao.insert(sequenceTable);
      });

   }

   private void initHttpClient() {
      cookies = new HashSet<>();
      OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new AddCookieIntercepter()).addInterceptor(new UpdateCookieIntercepter()).followRedirects(false).followSslRedirects(false).build();

      retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(localhost).client(client).build();
   }

   private void initOnlineSession() {
      onlineSessionHandler = new OnlineSessionHandler(firebaseDatabase);
      sessionRepository = onlineSessionHandler.getSessionRepository();
      dataLayerExecutor.execute(() -> cleanUpPreviousSession());
   }

   private void cleanUpPreviousSession() {
      File caches = getCacheDir();
      for (File file : caches.listFiles()) {
         if (file.getName().startsWith("SessionCache#")) {
            cleanUpDirectory(file);
         }
      }
      database.getPostDao().deleteAllPost();
      database.getCommentDao().deleteAllComment();
      database.getCommentDao().deleteAllReplyComment();
      database.getUserBasicInfoDao().deleteAll();
   }

   private void cleanUpDirectory(File dir) {
      for (File f : dir.listFiles()) {
         if (f.isFile()) {
            f.delete();
         } else if (f.isDirectory()) {
            cleanUpDirectory(f);
         }
      }
      dir.delete();
   }

   static private ApplicationContainer application;

   static public ApplicationContainer getInstance() {
      return application;
   }
}
