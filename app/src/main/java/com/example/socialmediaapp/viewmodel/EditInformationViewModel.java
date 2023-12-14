package com.example.socialmediaapp.viewmodel;

import android.content.Context;
import android.widget.Toast;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.application.session.UserSessionHandler;

import java.util.HashMap;

public class EditInformationViewModel extends ViewModel {
    private MediatorLiveData<String> postSubmitState;
    private MutableLiveData<String> fullname, alias, gender, birthday;

    public EditInformationViewModel() {
        super();
        postSubmitState = new MediatorLiveData<>();
        postSubmitState.setValue("Idle");
        fullname = new MutableLiveData<>();
        alias = new MutableLiveData<>();
        gender = new MutableLiveData<>();
        birthday = new MutableLiveData<>();
    }

    public MutableLiveData<String> getFullname() {
        return fullname;
    }

    public MutableLiveData<String> getAlias() {
        return alias;
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }

    public MutableLiveData<String> getBirthday() {
        return birthday;
    }

    public MediatorLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }
}
