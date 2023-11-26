package com.example.socialmediaapp.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.services.ServiceApi;

import retrofit2.Retrofit;

public class CreatePostViewModel extends ViewModel {
    private MutableLiveData<String> postStatusContent;
    private MutableLiveData<Uri> mediaContent;
    private String mediaType;
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
        postStatusContent = savedStateHandle.getLiveData("post status");
        mediaContent = savedStateHandle.getLiveData("media content");
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
    }

    public void postMyPost(HomePage host) {
        Bundle data = new Bundle();
        data.putString("status", postStatusContent.getValue());
        data.putString("media type", mediaType);
        Uri uri = mediaContent.getValue();
        data.putString("media content", (uri == null) ? null : uri.toString());
        PostFragment postFragment = (PostFragment) host.getFragment("posts");
        postFragment.uploadPost(data);
    }
}
