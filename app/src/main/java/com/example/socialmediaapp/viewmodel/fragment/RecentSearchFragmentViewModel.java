package com.example.socialmediaapp.viewmodel.fragment;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.application.repo.core.RecentSearchRepository;

public class RecentSearchFragmentViewModel extends ViewModel {
   private RecentSearchRepository itemRepository;
   private MediatorLiveData<Boolean> loadItemState;
   private boolean paused;

   public RecentSearchFragmentViewModel(RecentSearchAccessHandler recentHandler) {
      super();
      this.itemRepository = new RecentSearchRepository(recentHandler);

      loadItemState = new MediatorLiveData<>();
      loadItemState.setValue(false);
      paused = false;
   }

   public void load(int cnt) {
      if (loadItemState.getValue() || paused) return;
      loadItemState.setValue(true);

      LiveData<String> callBack = itemRepository.fetchNewItem(cnt);
      loadItemState.addSource(callBack, s -> {
         loadItemState.removeSource(callBack);
         loadItemState.setValue(false);
      });
   }

   public LiveData<String> onClickToUserProfile(String userId) {
      Bundle data = new Bundle();
      data.putString("user id", userId);
      return itemRepository.uploadNewItem(data);
   }

   public MutableLiveData<Boolean> getLoadItemState() {
      return loadItemState;
   }

   public RecentSearchRepository getItemRepository() {
      return itemRepository;
   }
}
