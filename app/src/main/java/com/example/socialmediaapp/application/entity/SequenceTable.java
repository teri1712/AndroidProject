package com.example.socialmediaapp.application.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SequenceTable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer head;
    private Integer tail;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHead() {
        return head;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public Integer getTail() {
        return tail;
    }

    public void setTail(Integer tail) {
        this.tail = tail;
    }
}
