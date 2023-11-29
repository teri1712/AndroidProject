package com.example.socialmediaapp.viewmodel;

import android.net.Uri;
import android.os.Bundle;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.home.fragment.CreatePostFragment;
import com.example.socialmediaapp.home.fragment.main.PostFragment;

public class CreatePostViewModel extends ViewModel {
    private MutableLiveData<String> postStatusContent;
    private MutableLiveData<Uri> mediaContent;
    private MediatorLiveData<Integer> cntEditedContent;
    private MutableLiveData<String> postSubmitState;
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
        postSubmitState = new MutableLiveData<>("Idle");
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

    public MutableLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }
}
