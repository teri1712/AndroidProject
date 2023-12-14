package com.example.socialmediaapp.viewmodel.models.repo.suck;

import java.util.HashMap;

public class Update {
    public enum Op {
        REMOVE, ADD, RECYCLE, END
    }

    public Op op;
    public HashMap<String, Object> data;

    public Update(Op op, HashMap<String, Object> data) {
        this.op = op;
        this.data = data;
    }
}
