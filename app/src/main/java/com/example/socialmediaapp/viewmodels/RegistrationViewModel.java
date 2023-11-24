package com.example.socialmediaapp.viewmodels;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.services.ServiceApi;

public class RegistrationViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> postSubmitState;
    private MutableLiveData<String> username, password, retypePassword;


    public RegistrationViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MutableLiveData<>("Idle");
        username = new MutableLiveData<>();
        password = new MutableLiveData<>();
        retypePassword = new MutableLiveData<>();
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
        ServiceApi.performSignUp(username.getValue(), password.getValue(), postSubmitState);
    }
}
