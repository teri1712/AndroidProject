package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;

public class RegistrationViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MediatorLiveData<String> postSubmitState;
    private MutableLiveData<String> username, password, retypePassword;
    private OnlineSessionHandler onlineSessionHandler = DecadeApplication.getInstance().onlineSessionHandler;

    public RegistrationViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MediatorLiveData<>();
        postSubmitState.setValue("Idle");
        username = savedStateHandle.getLiveData("username");
        password = savedStateHandle.getLiveData("password");
        retypePassword = savedStateHandle.getLiveData("retype password");
    }

    public MutableLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }

    public MutableLiveData<String> getRetypePassword() {
        return retypePassword;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void performSignUp() {
        MutableLiveData<String> callback = onlineSessionHandler.signUp(username.getValue(), password.getValue());
        postSubmitState.addSource(callback, s -> {
            postSubmitState.setValue(s);
            postSubmitState.removeSource(callback);
        });
    }
}
