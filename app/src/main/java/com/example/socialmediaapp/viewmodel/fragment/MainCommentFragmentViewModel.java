package com.example.socialmediaapp.viewmodel.fragment;

import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.models.post.base.PostModel;

public class MainCommentFragmentViewModel extends ViewModel {
  private SavedStateHandle savedStateHandle;
  private MutableLiveData<Uri> image;
  private MutableLiveData<String> content;
  private MediatorLiveData<Integer> cntEditedContent;
  private LiveData<String> sessionState;
  private MediatorLiveData<Integer> countLikeContent;
  private MediatorLiveData<String> sendState;


  public MainCommentFragmentViewModel(SavedStateHandle savedStateHandle) {
    super();
    this.savedStateHandle = savedStateHandle;
    content = savedStateHandle.getLiveData("comment content");
    image = savedStateHandle.getLiveData("image content");
    sendState = new MediatorLiveData<>();
    sendState.setValue("Idle");

    cntEditedContent = new MediatorLiveData<>();
    cntEditedContent.setValue(0);
    cntEditedContent.addSource(content, new Observer<String>() {
      @Override
      public void onChanged(String s) {
        int cur = cntEditedContent.getValue();
        if (s.isEmpty()) {
          cur ^= cntEditedContent.getValue() & 1;
        } else {
          cur |= 1;
        }
        cntEditedContent.setValue(cur);
      }
    });
    cntEditedContent.addSource(image, new Observer<Uri>() {
      @Override
      public void onChanged(Uri uri) {
        int cur = cntEditedContent.getValue();
        if (uri == null) {
          cur ^= cntEditedContent.getValue() & 2;
        } else {
          cur |= 2;
        }
        cntEditedContent.setValue(cur);
      }
    });
  }

  public void initCountLikeContent(LiveData<PostModel> postModel, LifecycleOwner lifecycleOwner) {
    countLikeContent = new MediatorLiveData<>();
    countLikeContent.setValue(0);
    postModel.observe(lifecycleOwner, new Observer<PostModel>() {
      @Override
      public void onChanged(PostModel postModel) {
        countLikeContent.setValue(calcLike(postModel.isLiked(), postModel.getLikeCount()));
      }
    });
  }

  private Integer calcLike(Boolean isLike, Integer cntLike) {
    return cntLike + (isLike ? 1 : 0);
  }

  public MutableLiveData<Integer> getCountLikeContent() {
    return countLikeContent;
  }


  public MediatorLiveData<String> getSendState() {
    return sendState;
  }

  public LiveData<String> getSessionState() {
    return sessionState;
  }

  public MutableLiveData<Uri> getImage() {
    return image;
  }

  public MutableLiveData<String> getContent() {
    return content;
  }

  public MediatorLiveData<Integer> getCntEditedContent() {
    return cntEditedContent;
  }

}
