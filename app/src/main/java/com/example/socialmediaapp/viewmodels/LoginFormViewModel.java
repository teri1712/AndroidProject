package com.example.socialmediaapp.viewmodels;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.apis.AuthenApi;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.services.ServiceApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
