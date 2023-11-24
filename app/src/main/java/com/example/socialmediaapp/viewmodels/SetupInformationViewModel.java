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

import java.sql.Date;

public class SetupInformationViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> postSubmitState;
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
        postSubmitState = new MutableLiveData<>("Idle");
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

    public void postMyPost(Context context) {
        if (postSubmitState.getValue().equals("In progress")) {
            Toast.makeText(context, "please wait until progress complete", Toast.LENGTH_SHORT).show();
            return;
        }
        postSubmitState.setValue("In progress");
        ServiceApi.setUpInformation(context, fullname.getValue(), alias.getValue(), gender.getValue(), birthday.getValue(), avatar.getValue(), postSubmitState);
    }
}
