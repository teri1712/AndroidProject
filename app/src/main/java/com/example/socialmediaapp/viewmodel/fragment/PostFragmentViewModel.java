package com.example.socialmediaapp.viewmodel.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.repo.core.UploadAdapter;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.repo.core.Repository;

public class PostFragmentViewModel extends ViewModel {
  private Repository<HandlerAccess> postRepo;
  private UploadAdapter<HandlerAccess> uploadAdapter;
  private MediatorLiveData<Boolean> loadPostState;
  private MediatorLiveData<Boolean> paused;

  public PostFragmentViewModel(Repository<HandlerAccess> repo) {
    this.postRepo = repo;
    loadPostState = new MediatorLiveData<>();
    loadPostState.setValue(false);
    paused = new MediatorLiveData<>();
    paused.setValue(false);
  }

  public void load(int cnt) {
    if (loadPostState.getValue() || paused.getValue()) return;
    loadPostState.setValue(true);

    LiveData<String> callBack = postRepo.fetchNewItem(cnt);
    loadPostState.addSource(callBack, length -> {
      loadPostState.removeSource(callBack);
      loadPostState.setValue(false);
    });
  }

  public MediatorLiveData<Boolean> getPaused() {
    return paused;
  }

  public MutableLiveData<Boolean> getLoadPostState() {
    return loadPostState;
  }

  public Repository<HandlerAccess> getPostRepo() {
    return postRepo;
  }
}
