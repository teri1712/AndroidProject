package com.example.socialmediaapp.models;

import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.UserSessionHandler;

public class MainPostFragmentViewModel extends ViewModel {
    private UserSessionHandler userSessionHandler;

    public MainPostFragmentViewModel(UserSessionHandler userSessionHandler) {
        super();
        this.userSessionHandler = userSessionHandler;
    }

}
