package com.example.socialmediaapp.viewmodels.models.repo;

public class Update<T> {
    public enum Op {
        REMOVE, ADD
    }

    private Op op;
    private int pos;

    public Update(Op op, int pos) {
        this.op = op;
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public Op getOp() {
        return op;
    }
}
