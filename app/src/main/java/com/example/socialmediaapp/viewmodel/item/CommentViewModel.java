package com.example.socialmediaapp.viewmodel.item;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.models.post.CommentModel;
import com.example.socialmediaapp.utils.TimeParserUtil;

public class CommentViewModel {
   private LiveData<CommentModel> commentModelLiveData;
   private MediatorLiveData<CommentModel> proxyLiveData;
   private LiveData<Integer> countLike;
   private LiveData<Boolean> like;
   private MediatorLiveData<String> countLikeContent;
   private LiveData<String> time;

   public CommentViewModel(LiveData<CommentModel> commentModelLiveData) {
      super();
      this.commentModelLiveData = commentModelLiveData;
      proxyLiveData = (MediatorLiveData<CommentModel>) Transformations.map(commentModelLiveData, input -> input);
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

   private void initLikePanelView() {
      countLike = Transformations.map(proxyLiveData, input -> input.getCountLike());
      like = Transformations.map(proxyLiveData, input -> input.isLiked());
   }

   private void updateCountLike(boolean b, int integer) {
      int value = integer + (b ? 1 : 0);
      countLikeContent.setValue(value == 0 ? "" : Integer.toString(value));
   }

   public LiveData<Boolean> getLike() {
      return like;
   }

   public LiveData<Integer> getCountLike() {
      return countLike;
   }

   public LiveData<String> getTime() {
      return time;
   }

   public MediatorLiveData<String> getCountLikeContent() {
      return countLikeContent;
   }
   public void clean() {
      proxyLiveData.removeSource(commentModelLiveData);
   }

}
