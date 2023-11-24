package com.example.socialmediaapp.viewmodels.models.repo.interceptor;

@FunctionalInterface
public interface UploadResultHandler<T> {
    void onResult(FetchResponse<T> itemWrapper);
}
