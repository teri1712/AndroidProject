package com.example.socialmediaapp.viewmodel.models.repo.callback;

import java.util.List;

@FunctionalInterface
public interface FetchResponseProcessor<T> {
    void onResponse(DataEmit<List<T>> res);
}
