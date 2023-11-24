package com.example.socialmediaapp.viewmodels.models.repo;

import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

public class ItemRepository<T> {
    private List<T> list;
    private MediatorLiveData<Update<T>> updateOnList;

    public ItemRepository() {
        list = new ArrayList<>();
        updateOnList = new MediatorLiveData<>();
    }

    public void addToEnd(T p) {
        list.add(p);
        updateOnList.setValue(new Update<>(Update.Op.ADD, list.size() - 1));
        updateOnList.setValue(null);
    }

    public void addToBegin(T p) {
        list.add(0, p);
        updateOnList.setValue(new Update<>(Update.Op.ADD, 0));
        updateOnList.setValue(null);
    }

    public T getItem(int pos) {
        return list.get(pos);
    }

    public MediatorLiveData<Update<T>> getUpdateOnRepo() {
        return updateOnList;
    }

    public List<T> findAllItem() {
        return list;
    }

    public void remove(T p) {
        int pos = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(p)) {
                pos = i;
            }
        }
        if (pos == -1) return;
        list.remove(pos);
        updateOnList.setValue(new Update<>(Update.Op.REMOVE, pos));
        updateOnList.setValue(null);

    }
}
