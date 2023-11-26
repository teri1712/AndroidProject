package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.services.ServiceApi;

public class LoginFormViewModel extends ViewModel {
    private MutableLiveData<String> username;
    private MutableLiveData<String> password;
    private MutableLiveData<String> authenticationState;

    private SavedStateHandle handle;


    public LoginFormViewModel(SavedStateHandle handle) {
        super();
        this.handle = handle;
        username = new MutableLiveData<>("");
        password = new MutableLiveData<>("");
        authenticationState = new MutableLiveData<>("Idle");
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
        ServiceApi.performAuthentication(username.getValue(), password.getValue(), authenticationState);
    }
}
