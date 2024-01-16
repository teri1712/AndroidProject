package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.repo.core.FriendRequestRepository;

public class FriendFragmentViewModel extends ViewModel {
   private FriendRequestRepository repo;
   private MediatorLiveData<Boolean> loadState;

   public FriendFragmentViewModel(FriendRequestRepository repo) {
      super();
      this.repo = repo;
      loadState = new MediatorLiveData<>();
      loadState.setValue(false);
   }


   public MutableLiveData<Boolean> getLoadState() {
      return loadState;
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
