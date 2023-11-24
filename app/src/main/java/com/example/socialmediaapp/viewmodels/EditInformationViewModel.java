package com.example.socialmediaapp.viewmodels;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.services.ServiceApi;

public class EditInformationViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<String> postSubmitState;
    private MutableLiveData<UpdateUserRequestBody> userInfo;

    private MutableLiveData<String> fullname, alias, gender, birthday;

    public MutableLiveData<UpdateUserRequestBody> getUserInfo() {
        return userInfo;
    }

    public EditInformationViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        postSubmitState = new MutableLiveData<>("Idle");
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


    public void postMyPost(Context context) {
        if (postSubmitState.getValue().equals("In progress")) {
            Toast.makeText(context, "please wait until progress complete", Toast.LENGTH_SHORT).show();
            return;
        }
        postSubmitState.setValue("In progress");

        ServiceApi.changeInformation(context, postSubmitState, fullname.getValue(), alias.getValue(), gender.getValue(), birthday.getValue());
    }
}
