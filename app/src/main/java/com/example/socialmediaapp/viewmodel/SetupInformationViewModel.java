package com.example.socialmediaapp.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.api.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.application.session.SelfProfileSessionHandler;

import java.util.HashMap;

public class SetupInformationViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MediatorLiveData<String> postSubmitState;
    private MutableLiveData<UpdateUserRequestBody> userInfo;

    private LiveData<String> fullname, alias, gender, birthday;
    private MutableLiveData<Uri> avatar;
    private MutableLiveData<String> curSession;
    private MutableLiveData<Object> recovery;

    private Observer<? extends Object> currentSessionDataObserver;

    public MutableLiveData<String> getPostSubmitState() {
        return postSubmitState;
    }

    public MutableLiveData<String> getCurSession() {
        return curSession;
    }

    public Observer<? extends Object> getCurrentSessionDataObserver() {
        return currentSessionDataObserver;
    }

    public void setCurrentSessionDataObserver(Observer<? extends Object> currentSessionDataObserver) {
        this.currentSessionDataObserver = currentSessionDataObserver;
    }

    public LiveData<String> getFullname() {
        return fullname;
    }

    public LiveData<String> getAlias() {
        return alias;
    }

    public LiveData<String> getGender() {
        return gender;
    }

    public LiveData<String> getBirthday() {
        return birthday;
    }

    public MutableLiveData<Object> getRecovery() {
        return recovery;
    }

    public SetupInformationViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MediatorLiveData<>();
        postSubmitState.setValue("Idle");
        userInfo = new MutableLiveData<>();
        curSession = new MutableLiveData<>();
        avatar = new MutableLiveData<>();
        recovery = new MutableLiveData<>();

        fullname = Transformations.map(userInfo, new Function<UpdateUserRequestBody, String>() {
            @Override
            public String apply(UpdateUserRequestBody input) {
                return input.getFullname();
            }
        });
        alias = Transformations.map(userInfo, new Function<UpdateUserRequestBody, String>() {
            @Override
            public String apply(UpdateUserRequestBody input) {
                return input.getAlias();
            }
        });
        gender = Transformations.map(userInfo, new Function<UpdateUserRequestBody, String>() {
            @Override
            public String apply(UpdateUserRequestBody input) {
                return input.getGender();
            }
        });
        birthday = Transformations.map(userInfo, new Function<UpdateUserRequestBody, String>() {
            @Override
            public String apply(UpdateUserRequestBody input) {
                return input.getBirthday();
            }
        });
    }

    public MutableLiveData<UpdateUserRequestBody> getUserInfo() {
        return userInfo;
    }

    public MutableLiveData<Uri> getAvatar() {
        return avatar;
    }

    public void send(Context context, SelfProfileSessionHandler selfProfileSessionHandler) {
        if (postSubmitState.getValue().equals("In progress")) {
            Toast.makeText(context, "please wait until progress complete", Toast.LENGTH_SHORT).show();
            return;
        }
        postSubmitState.setValue("In progress");
        Bundle data = new Bundle();
        data.putString("fullname", fullname.getValue());
        data.putString("alias", alias.getValue());
        data.putString("gender", gender.getValue());
        data.putString("birthday", birthday.getValue());
        Uri uri = avatar.getValue();
        data.putString("avatar", uri == null ? null : uri.toString());

        LiveData<String> callBack = selfProfileSessionHandler.setUpInformation(data);
        postSubmitState.addSource(callBack, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                postSubmitState.setValue(s);
                postSubmitState.removeSource(callBack);
            }
        });
    }
}
