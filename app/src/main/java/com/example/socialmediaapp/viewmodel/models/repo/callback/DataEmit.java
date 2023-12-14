package com.example.socialmediaapp.viewmodel.models.repo.callback;

import java.util.HashMap;

public class DataEmit {
    private String status;
    private String type;
    private HashMap<String, Object> data;

    public DataEmit(HashMap<String, Object> data, String status, String type) {
        this.status = status;
        this.data = data;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
