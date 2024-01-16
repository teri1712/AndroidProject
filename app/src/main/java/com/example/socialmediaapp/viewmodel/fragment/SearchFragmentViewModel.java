package com.example.socialmediaapp.viewmodel.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.SearchSessionHandler;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.util.List;

public class SearchFragmentViewModel extends ViewModel {
   private MediatorLiveData<List<UserBasicInfoModel>> searchResult;
   private SearchSessionHandler searchSessionHandler;
   private MutableLiveData<String> loadSearchResultState;

   public SearchFragmentViewModel(SearchSessionHandler searchSessionHandler) {
      super();
      this.searchSessionHandler = searchSessionHandler;
      searchResult = new MediatorLiveData<>();
      loadSearchResultState = new MutableLiveData<>("idle");
   }

   public MediatorLiveData<List<UserBasicInfoModel>> getSearchResult() {
      return searchResult;
   }
   private LiveData<List<UserBasicInfoModel>> curSearchSource;

   public void searchForUser(String query) {
      LiveData<List<UserBasicInfoModel>> callBack = searchSessionHandler.searchForUser(query);
      if (curSearchSource != null) {
         searchResult.removeSource(curSearchSource);
      }
      curSearchSource = callBack;
      if (loadSearchResultState.getValue().equals("idle"))
         loadSearchResultState.setValue("loading");
      searchResult.addSource(callBack, new Observer<List<UserBasicInfoModel>>() {
         @Override
         public void onChanged(List<UserBasicInfoModel> userBasicInfoModels) {
            loadSearchResultState.setValue("idle");
            searchResult.setValue(userBasicInfoModels);
         }
      });
   }

   public MutableLiveData<String> getLoadSearchResultState() {
      return loadSearchResultState;
   }
}
