package com.example.socialmediaapp.viewmodel.models.repo.suck;

public class Update<T> {
    public enum Op {
        REMOVE, ADD
    }

    private Op op;
    private T item;
    private int pos;

    public Update(Op op, T item, int pos) {
        this.op = op;
        this.item = item;
        this.pos = pos;
    }

    public T getItem() {
        return item;
    }

    public int getPos() {
        return pos;
    }

    public Op getOp() {
        return op;
    }
}
