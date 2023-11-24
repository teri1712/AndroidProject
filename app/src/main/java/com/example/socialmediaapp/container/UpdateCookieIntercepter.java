package com.example.socialmediaapp.container;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateCookieIntercepter implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request req = chain.request();

        Response res = chain.proceed(chain.request());


        Set<String> cookies = ApplicationContainer.getInstance().cookies;
        synchronized (cookies) {
            for (String c : res.headers("Set-Cookie")) {
                System.out.println(c);
                cookies.add(c);
            }
        }
        return res;
    }
}
