package com.example.socialmediaapp.viewmodel.fragment;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.repo.core.UploadAdapter;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.session.ReplyAccessHandler;
import com.example.socialmediaapp.application.repo.core.ReplyCommentRepository;
import com.example.socialmediaapp.application.session.ReplyUploadTask;

public class ReplyListViewModel {
  private ReplyCommentRepository replyRepo;
  private MediatorLiveData<Boolean> loadState;
  private MediatorLiveData<Integer> countUnRead;
  private UploadAdapter<HandlerAccess> uploadAdapter;

  public ReplyListViewModel(ReplyAccessHandler accessHandler) {
    super();
    replyRepo = new ReplyCommentRepository(accessHandler);
    uploadAdapter = new UploadAdapter<>(ReplyUploadTask.class);
    replyRepo.setUploadAdapter(uploadAdapter);

    loadState = new MediatorLiveData<>();
    loadState.setValue(false);
    countUnRead = replyRepo.getCountUnLoadedComment();
  }

  public MediatorLiveData<Integer> getCountUnRead() {
    return countUnRead;
  }

  public LiveData<String> upload(Bundle data) {
    return uploadAdapter.uploadNewItem(data);
  }

  public void load(int cnt) {
    if (loadState.getValue()) return;

    loadState.setValue(true);

    LiveData<String> callBack = replyRepo.fetchNewItem(cnt);
    loadState.addSource(callBack, s -> {
      loadState.removeSource(callBack);
      loadState.setValue(false);
      ;
    });
  }

  public ReplyCommentRepository getReplyRepo() {
    return replyRepo;
  }

  public MutableLiveData<Boolean> getLoadState() {
    return loadState;
  }
}
