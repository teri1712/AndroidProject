package com.example.socialmediaapp.viewmodel.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.models.post.ReplyModel;
import com.example.socialmediaapp.utils.TimeParserUtil;

public class ReplyViewModel {
   private MediatorLiveData<ReplyModel> proxyLiveData;
   private LiveData<ReplyModel> replyModelLiveData;
   private LiveData<Integer> countLike;
   private LiveData<Boolean> like;
   private MediatorLiveData<String> countLikeContent;
   private LiveData<String> time;

   public ReplyViewModel(LiveData<ReplyModel> replyModelLiveData) {
      super();
      this.replyModelLiveData = replyModelLiveData;
      this.proxyLiveData = (MediatorLiveData<ReplyModel>) Transformations.map(replyModelLiveData, input -> input);
      time = Transformations.map(proxyLiveData, input -> TimeParserUtil.parseTime(input.getTime()));
      initLikePanelView();
      countLikeContent = new MediatorLiveData<>();
      countLikeContent.addSource(countLike, integer -> {
         if (integer == null || like.getValue() == null) return;
         updateCountLike(like.getValue(), integer);
      });
      countLikeContent.addSource(like, b -> {
         if (b == null || countLike.getValue() == null) return;
         updateCountLike(b, countLike.getValue());
      });

   }

   private void updateCountLike(boolean b, int integer) {
      int value = integer + (b ? 1 : 0);
      countLikeContent.setValue(value == 0 ? "" : Integer.toString(value));
   }

   private void initLikePanelView() {
      countLike = Transformations.map(proxyLiveData, input -> input.getCountLike());
      like = Transformations.map(proxyLiveData, input -> input.isLiked());
   }

   public LiveData<String> getTime() {
      return time;
   }

   public LiveData<Boolean> getLike() {
      return like;
   }

   public LiveData<String> getCountLikeContent() {
      return countLikeContent;
   }

   public void clean() {
      proxyLiveData.removeSource(replyModelLiveData);
   }
}
