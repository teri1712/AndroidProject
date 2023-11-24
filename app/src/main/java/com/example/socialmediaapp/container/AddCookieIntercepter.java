package com.example.socialmediaapp.container;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddCookieIntercepter implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request req = chain.request();

        Request.Builder builder = req.newBuilder();
        Set<String> cookies = ApplicationContainer.getInstance().cookies;
        synchronized (cookies) {
            for (String c : cookies) builder.addHeader("Cookie", c);
        }
        return chain.proceed(builder.build());
    }
}
