package com.example.socialmediaapp.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.services.ServiceApi;

public class UpdateBackgroundViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> postSubmitState;
    private MutableLiveData<String> postStatusContent;
    private MutableLiveData<Uri> imageUri;
    private final String mediaType = "image";

    public UpdateBackgroundViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MutableLiveData<>("Idle");
        postStatusContent = savedStateHandle.getLiveData("post content");
        imageUri = new MutableLiveData<>();
    }


    public MutableLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }

    public MutableLiveData<String> getPostStatusContent() {
        return postStatusContent;
    }

    public MutableLiveData<Uri> getImageUri() {
        return imageUri;
    }

    public void setImageUri(MutableLiveData<Uri> imageUri) {
        this.imageUri = imageUri;
    }

}
