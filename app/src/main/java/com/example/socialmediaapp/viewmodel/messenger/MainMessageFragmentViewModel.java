package com.example.socialmediaapp.viewmodel.messenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainMessageFragmentViewModel extends ViewModel {
   private MutableLiveData<String> msgContent;
   private LiveData<String> sessionState;
   private MutableLiveData<List<String>> imageSelected;

   public MainMessageFragmentViewModel(SavedStateHandle savedStateHandle) {
      super();
      msgContent = savedStateHandle.getLiveData("message content");
      imageSelected = new MutableLiveData<>(new ArrayList<>());
   }

   public MutableLiveData<List<String>> getImageSelected() {
      return imageSelected;
   }
   public LiveData<String> getSessionState() {
      return sessionState;
   }

   public MutableLiveData<String> getMsgContent() {
      return msgContent;
   }

}
