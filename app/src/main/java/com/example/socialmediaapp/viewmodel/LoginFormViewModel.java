package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.services.ServiceApi;

public class LoginFormViewModel extends ViewModel {
    private MutableLiveData<String> username;
    private MutableLiveData<String> password;
    private MediatorLiveData<String> authenticationState;
    private SavedStateHandle handle;
    private OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;

    public LoginFormViewModel(SavedStateHandle handle) {
        super();
        this.handle = handle;
        username = handle.getLiveData("username");
        password = handle.getLiveData("password");
        authenticationState = new MediatorLiveData<>();
        authenticationState.setValue("Idle");
    }

    public MutableLiveData<String> getAuthenticationState() {
        return authenticationState;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }


    public void performAuthentication() {
        MutableLiveData<String> callBack = onlineSessionHandler.authenticate(username.getValue(), password.getValue());
        authenticationState.addSource(callBack, s -> {
            authenticationState.setValue(s);
            authenticationState.removeSource(callBack);
        });
    }
}
