package com.example.socialmediaapp.viewmodels.factory;

import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.viewmodels.refactor.DataViewModel;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

public class DataViewModelFactory {

    static public <T> DataViewModel<T> get(T data, SessionHandler.SessionRegistry sessionRegistry) {
        if (data instanceof Post) {
        }
        return null;
    }
}
