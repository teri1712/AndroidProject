package com.example.socialmediaapp.viewmodels;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.services.ServiceApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatePostViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> postStatusContent;
    private MutableLiveData<Uri> mediaContent;

    private MutableLiveData<String> postSubmitState;

    private String mediaType;

    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;

    private MediatorLiveData<Integer> cntEditedContent;

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public MutableLiveData<String> getPostStatusContent() {
        return postStatusContent;
    }

    public MutableLiveData<Uri> getMediaContent() {
        return mediaContent;
    }

    public MutableLiveData<Integer> getCntEditedContent() {
        return cntEditedContent;
    }

    public CreatePostViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postStatusContent = new MutableLiveData<>();
        mediaContent = new MutableLiveData<>();
        cntEditedContent = new MediatorLiveData<>();
        cntEditedContent.setValue(0);
        cntEditedContent.addSource(postStatusContent, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                int cur = cntEditedContent.getValue();
                if (s.isEmpty()) {
                    cur ^= cntEditedContent.getValue() & 1;
                } else {
                    cur |= 1;
                }
                cntEditedContent.setValue(cur);
            }
        });
        cntEditedContent.addSource(mediaContent, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                int cur = cntEditedContent.getValue();
                if (uri == null) {
                    cur ^= cntEditedContent.getValue() & 2;
                } else {
                    cur |= 2;
                }
                cntEditedContent.setValue(cur);
            }
        });
        postSubmitState = new MutableLiveData<>("Idle");
    }

    public MutableLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }

    public void postMyPost(Context context) {
        if (postSubmitState.getValue().equals("In progress")) {
            Toast.makeText(context, "Please wait while posting in progress", Toast.LENGTH_SHORT).show();
            return;
        }
        postSubmitState.setValue("In progress");

        ServiceApi.postMyPosts(context, postStatusContent.getValue(), mediaType, mediaContent.getValue(), postSubmitState);
    }
}
