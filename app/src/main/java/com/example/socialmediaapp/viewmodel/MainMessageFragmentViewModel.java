package com.example.socialmediaapp.viewmodel;

import android.net.Uri;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.SessionHandler;

public class MainMessageFragmentViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> messageContent;
    private LiveData<String> sessionState;
    private MediatorLiveData<String> sendMessageState;
    private MutableLiveData<Uri> image;


    public MainMessageFragmentViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        messageContent = savedStateHandle.getLiveData("comment content");
        sendMessageState = new MediatorLiveData<>();
        sendMessageState.setValue("Idle");
        image = savedStateHandle.getLiveData("image content");

    }

    public MediatorLiveData<String> getSendMessageState() {
        return sendMessageState;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }

    public MutableLiveData<Uri> getImage() {
        return image;
    }

    public MutableLiveData<String> getMessageContent() {
        return messageContent;
    }

}
