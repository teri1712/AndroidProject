package com.example.socialmediaapp.viewmodel.fragment;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.repo.core.CommentRepository;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;

public class PostDetailsFragmentViewModel extends ViewModel {
  private SavedStateHandle savedStateHandle;
  private MutableLiveData<Uri> image;
  private MutableLiveData<String> content;
  private MediatorLiveData<String> sendState;
  private Repository<HandlerAccess> repo;
  private MediatorLiveData<Integer> cntEditedContent;
  private MediatorLiveData<Boolean> loadState;
  private boolean paused;
  private MediatorLiveData<Boolean> hintComment;

  public PostDetailsFragmentViewModel(SavedStateHandle savedStateHandle) {
    this.savedStateHandle = savedStateHandle;
    content = savedStateHandle.getLiveData("comment content");
    image = savedStateHandle.getLiveData("image content");
    loadState = new MediatorLiveData<>();
    loadState.setValue(false);
    sendState = new MediatorLiveData<>();
    sendState.setValue("Idle");
    paused = false;

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

  public void setRepo(CommentRepository repo) {
    this.repo = repo;
    LiveData<Update> itemUpdate = this.repo.getItemUpdate();
    hintComment = new MediatorLiveData<>();
    hintComment.addSource(itemUpdate, update -> {
      if (update == null) return;
      Update.Op op = update.op;
      if (op == Update.Op.ADD) {
        int length = (int) update.data.get("length");
        if (length == 0) {
          paused = true;
        }
      }
      if (op == Update.Op.HINT_UPDATE) {
        if (!loadState.getValue()) {
          hintComment.setValue(true);
        }
      }
    });
  }

  public MediatorLiveData<Integer> getCntEditedContent() {
    return cntEditedContent;
  }

  public MutableLiveData<Uri> getImage() {
    return image;
  }

  public void load(int cnt) {
    if (loadState.getValue() || paused) return;
    loadState.setValue(true);

    LiveData<String> callBack = repo.fetchNewItem(cnt);
    loadState.addSource(callBack, length -> {
      loadState.removeSource(callBack);
      loadState.setValue(false);
    });
  }

  public MutableLiveData<String> getContent() {
    return content;
  }

  public MediatorLiveData<String> getSendState() {
    return sendState;
  }

  public LiveData<Boolean> getHintComment() {
    return hintComment;
  }

  public Repository<HandlerAccess> getRepo() {
    return repo;
  }

  public LiveData<Boolean> getLoadState() {
    return loadState;
  }
}
