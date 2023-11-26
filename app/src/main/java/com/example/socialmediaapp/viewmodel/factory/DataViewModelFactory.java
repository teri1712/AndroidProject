package com.example.socialmediaapp.viewmodel.factory;

import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.refactor.DataViewModel;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;

public class DataViewModelFactory {

    static public <T> DataViewModel<T> get(T data, SessionHandler.SessionRegistry sessionRegistry) {
        if (data instanceof Post) {
        }
        return null;
    }
}
