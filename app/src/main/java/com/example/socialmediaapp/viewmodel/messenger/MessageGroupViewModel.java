package com.example.socialmediaapp.viewmodel.messenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.viewmodel.models.messenger.MessageGroup;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

public class MessageGroupViewModel {
    static private int idSeq = 0;
    private MutableLiveData<Update> itemUpdate;
    private Integer uid;
    private MessageGroup message;
    private boolean enclosed;

    public MessageGroupViewModel(MessageGroup message) {
        this.message = message;
        itemUpdate = new MutableLiveData<>();
        enclosed = false;
        uid = idSeq++;
    }

    public boolean isEnclosed() {
        return enclosed;
    }

    public Integer getUid() {
        return uid;
    }

    public void setEnclosed(boolean enclosed) {
        this.enclosed = enclosed;
    }

    public MessageGroup getMessageGroup() {
        return message;
    }

    public MutableLiveData<Update> getItemUpdate() {
        return itemUpdate;
    }
}
