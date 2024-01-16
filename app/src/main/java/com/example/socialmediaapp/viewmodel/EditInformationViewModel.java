package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
