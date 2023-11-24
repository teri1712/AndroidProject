package com.example.socialmediaapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.socialmediaapp.apis.AuthenApi;
import com.example.socialmediaapp.container.ApplicationContainer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthenticationService extends Service {
    private IBinder binder;
    private BlockingQueue<Runnable> blockingQueue;

    //catch the
    private Boolean isDestroyed;
    private Retrofit retrofit;

    public class ServiceBinder extends Binder {
        public AuthenticationService getService() {
            return AuthenticationService.this;
        }
    }

    public interface onAuthenticationResult {
        public void onResult(final String res);
    }

    public void attemptAuthenticate(final String username, final String password, onAuthenticationResult callback) {
        //put it in the process queue
        try {
            blockingQueue.put(new Runnable() {
                @Override
                public void run() {
                    AuthenApi request = retrofit.create(AuthenApi.class);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate() {
        retrofit = ApplicationContainer.getInstance().retrofit;
        isDestroyed = false;
        binder = new ServiceBinder();
        blockingQueue = new ArrayBlockingQueue<>(5);
        // service handler thread, consider custom instead of looper because i want to register a call back
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isDestroyed) {
                    try {
                        Runnable get = blockingQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (get != null) {
                            get.run();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
    }
}
