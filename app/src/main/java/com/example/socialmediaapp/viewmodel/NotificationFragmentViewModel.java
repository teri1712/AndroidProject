package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.repo.core.NotificationRepository;

public class NotificationFragmentViewModel extends ViewModel {
   private NotificationRepository repository;
   private MediatorLiveData<Boolean> loadState;
   private boolean paused;

   public NotificationFragmentViewModel(NotificationRepository repository) {
      super();
      this.repository = repository;
      loadState = new MediatorLiveData<>();
      loadState.setValue(false);
      paused = false;
   }


   public MutableLiveData<Boolean> getLoadState() {
      return loadState;
   }

   public void setPaused(boolean paused) {
      this.paused = paused;
   }

   public void load(int cnt) {
      if (loadState.getValue() || paused) return;

      loadState.setValue(true);
      LiveData<String> callBack = repository.fetchNewItem(cnt);
      loadState.addSource(callBack, s -> {
         loadState.removeSource(callBack);
         loadState.setValue(false);
      });
   }

}
