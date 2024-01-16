package com.example.socialmediaapp.viewmodel.messenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.MessageAccessHandler;
import com.example.socialmediaapp.application.repo.core.MessageRepository;

public class MessageFragmentViewModel extends ViewModel {
  private MessageRepository repo;
  private MediatorLiveData<Boolean> loadState;

  public MessageFragmentViewModel(MessageAccessHandler handler) {
    super();
    repo = new MessageRepository(handler);
    loadState = new MediatorLiveData<>();
    loadState.setValue(false);
  }

  public MutableLiveData<Boolean> getLoadState() {
    return loadState;
  }

  public MessageRepository getRepo() {
    return repo;
  }

  public void load(int cnt) {
    if (loadState.getValue()) return;

    loadState.setValue(true);
    LiveData<String> callBack = repo.fetchNewItem(cnt);
    loadState.addSource(callBack, s -> {
      loadState.removeSource(callBack);
      loadState.setValue(false);
    });
  }

}
