package com.example.socialmediaapp.viewmodel;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class UpdateAvatarViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> postSubmitState;
    private MutableLiveData<String> postStatusContent;
    private MutableLiveData<Uri> imageUri;
    private final String mediaType = "image";

    public UpdateAvatarViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MutableLiveData<>("Idle");
        postStatusContent = savedStateHandle.getLiveData("post status");
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

}
