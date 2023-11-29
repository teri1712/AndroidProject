package com.example.socialmediaapp.viewmodel.models.repo.callback;

import java.util.List;

@FunctionalInterface
public interface FetchResponseProcessor<T> {
    void onResponse(FetchResponse<List<T>> res);
}
