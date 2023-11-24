package com.example.socialmediaapp.viewmodels.models.repo.interceptor;

import java.util.List;

@FunctionalInterface
public interface FetchResponseProcessor<T> {
    void onResponse(FetchResponse<List<T>> res);
}
