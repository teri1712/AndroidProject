package com.example.socialmediaapp.viewmodel.models.repo.interceptor;

@FunctionalInterface
public interface UploadResultHandler<T> {
    void onResult(FetchResponse<T> itemWrapper);
}
