package com.example.socialmediaapp.viewmodel.fragment;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.CommentAccessHandler;
import com.example.socialmediaapp.application.repo.core.CommentRepository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;

public class CommentFragmentViewModel extends ViewModel {
   private CommentRepository repo;
   private MediatorLiveData<Boolean> loadState;
   private boolean paused;
   private MediatorLiveData<Boolean> newComment;

   public CommentFragmentViewModel(CommentAccessHandler accessHandler) {
      super();
      this.repo = new CommentRepository(accessHandler);
      loadState = new MediatorLiveData<>();
      loadState.setValue(false);
      paused = false;

      LiveData<Update> itemUpdate = repo.getItemUpdate();
      newComment = new MediatorLiveData<>();
      newComment.addSource(itemUpdate, update -> {
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
               newComment.setValue(true);
            }
         }
      });
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

   public CommentRepository getRepo() {
      return repo;
   }

   public MediatorLiveData<Boolean> getNewComment() {
      return newComment;
   }

   public MutableLiveData<Boolean> getLoadState() {
      return loadState;
   }

}
