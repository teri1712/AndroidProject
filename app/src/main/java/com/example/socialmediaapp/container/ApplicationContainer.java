package com.example.socialmediaapp.container;


import android.app.Application;

import androidx.room.Room;
import androidx.work.WorkManager;

import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.container.database.AppDatabase;
import com.example.socialmediaapp.container.session.CommentSessionHandler;
import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.container.session.OnlineSessionHandler;
import com.example.socialmediaapp.container.session.PostSessionHandler;
import com.example.socialmediaapp.viewmodel.models.HomePageContent;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
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
    public final String localhost = "http://192.168.0.106:8080";
    public Retrofit retrofit;
    public Set<String> cookies;
    public AppDatabase database;
    public WorkManager workManager;
    public HashMap<String, ExecutorService> executors;
    public OnlineSessionHandler onlineSessionHandler;
    public Executor dataLayerExecutor = Executors.newSingleThreadExecutor();

    private ApplicationContainer() {
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "MyApp").build();
        workManager = WorkManager.getInstance(getApplicationContext());
        cookies = new HashSet<>();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();
        executors = new HashMap<>();
        onlineSessionHandler = new OnlineSessionHandler();

        //for debugging
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new AddCookieIntercepter()).addInterceptor(new UpdateCookieIntercepter()).followRedirects(false)
                .followSslRedirects(false).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(localhost)
                .addConverterFactory(GsonConverterFactory.create(gson)).callbackExecutor(Executors.newSingleThreadExecutor())
                .client(client)
                .build();
    }

    static private ApplicationContainer applicationContainer;

    static {
        applicationContainer = new ApplicationContainer();
    }

    static public ApplicationContainer getInstance() {
        return applicationContainer;
    }
}
