package com.example.socialmediaapp.viewmodel.models.repo.interceptor;

public class FetchResponse<T> {
    private String status;
    private T data;

    public FetchResponse(T data, String status) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
