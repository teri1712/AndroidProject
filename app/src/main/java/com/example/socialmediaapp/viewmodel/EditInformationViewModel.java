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
    private SavedStateHandle savedStateHandle;
    private MediatorLiveData<String> postSubmitState;
    private MutableLiveData<UpdateUserRequestBody> userInfo;

    private MutableLiveData<String> fullname, alias, gender, birthday;

    public MutableLiveData<UpdateUserRequestBody> getUserInfo() {
        return userInfo;
    }

    public EditInformationViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MediatorLiveData<>();
        postSubmitState.setValue("Idle");
        userInfo = new MutableLiveData<>();
        fullname = (MutableLiveData<String>) Transformations.switchMap(userInfo, new Function<UpdateUserRequestBody, LiveData<String>>() {
            @Override
            public LiveData<String> apply(UpdateUserRequestBody input) {
                return new MutableLiveData<String>(input.getFullname());
            }
        });
        alias = (MutableLiveData<String>) Transformations.switchMap(userInfo, new Function<UpdateUserRequestBody, LiveData<String>>() {
            @Override
            public LiveData<String> apply(UpdateUserRequestBody input) {
                return new MutableLiveData<String>(input.getAlias());
            }
        });
        gender = (MutableLiveData<String>) Transformations.switchMap(userInfo, new Function<UpdateUserRequestBody, LiveData<String>>() {
            @Override
            public LiveData<String> apply(UpdateUserRequestBody input) {
                return new MutableLiveData<String>(input.getGender());
            }
        });
        birthday = (MutableLiveData<String>) Transformations.switchMap(userInfo, new Function<UpdateUserRequestBody, LiveData<String>>() {
            @Override
            public LiveData<String> apply(UpdateUserRequestBody input) {
                return new MutableLiveData<String>(input.getBirthday());
            }
        });
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
    public MutableLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }
    public void send(Context context, UserSessionHandler userSessionHandler) {
        if (postSubmitState.getValue().equals("In progress")) {
            Toast.makeText(context, "please wait until progress complete", Toast.LENGTH_SHORT).show();
            return;
        }
        postSubmitState.setValue("In progress");
        HashMap<String, String> data =new HashMap<>();
        data.put("fullname", fullname.getValue());
        data.put("alias", alias.getValue());
        data.put("gender", gender.getValue());
        data.put("birthday", birthday.getValue());
        postSubmitState.addSource(userSessionHandler.changeInformation(data), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                postSubmitState.setValue(s);
            }
        });
    }
}
