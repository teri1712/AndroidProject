package com.example.socialmediaapp.container.converter;

import android.content.ContentResolver;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class HttpBodyConverter {

    static private RequestBody mediaStreamBody(ContentResolver resolver, Uri uri) throws FileNotFoundException {
        RequestBody body = null;
        InputStream is = resolver.openInputStream(uri);
        body = new RequestBody() {
            @Nullable
            public MediaType contentType() {
                return MediaType.parse(resolver.getType(uri));
            }

            public long contentLength() {
                try {
                    return is.available();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            public void writeTo(@NotNull BufferedSink sink) {
                byte[] buffer = new byte[2048];
                long bread = 0;
                try {
                    long total_length = is.available();
                    while (bread != total_length) {
                        try {
                            int cnt = is.read(buffer, 0, Math.min(2048, (int) (total_length - bread)));
                            sink.write(buffer, 0, cnt);
                            sink.flush();
                            bread += cnt;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return body;
    }

    static public RequestBody getTextRequestBody(String content) {
        if (content == null) return null;
        return RequestBody.create(content, MediaType.parse("text/plain"));
    }

    static public MultipartBody.Part getMultipartBody(Uri uri, ContentResolver contentResolver,String name) throws FileNotFoundException {
        return MultipartBody.Part.createFormData(name, "", mediaStreamBody(contentResolver, uri));
    }
}
