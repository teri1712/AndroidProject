package com.example.socialmediaapp.viewmodel.item;

import android.graphics.Bitmap;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.models.post.base.PostModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.TimeParserUtil;

import java.util.BitSet;

public class PostViewModel {
   private LiveData<String> hostName;
   private LiveData<PostModel> postModelLiveData;
   private MediatorLiveData<PostModel> proxyLiveData;
   private LiveData<Integer> countLike, countComment, countShare;
   private MediatorLiveData<String> countLikeContent;
   private LiveData<Boolean> like;
   private LiveData<String> time;

   public PostViewModel(
           LiveData<PostModel> postModelLiveData,
           LiveData<String> hostName) {
      super();
      this.postModelLiveData = postModelLiveData;
      this.hostName = hostName;
      this.proxyLiveData = (MediatorLiveData<PostModel>) Transformations.map(postModelLiveData, input -> input);
      this.time = Transformations.map(proxyLiveData, input -> TimeParserUtil.parseTime(input.getTime()));
      initLikePanelView();
      this.countLikeContent = new MediatorLiveData<>();
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
      countLike = Transformations.map(proxyLiveData, input -> input.getLikeCount());
      countComment = Transformations.map(proxyLiveData, input -> input.getCommentCount());
      countShare = Transformations.map(proxyLiveData, input -> input.getShareCount());
      like = Transformations.map(proxyLiveData, input -> input.isLiked());
   }

   private void updateCountLike(boolean b, int integer) {
      String pref = b ? "You and " : "";
      String suf = b ? " others" : "";
      if (integer != 0) {
         countLikeContent.setValue(pref + integer + suf);
      } else {
         if (!b) {
            countLikeContent.setValue("");
         } else {
            countLikeContent.addSource(hostName, s -> {
               countLikeContent.setValue(s);
               countLikeContent.removeSource(hostName);
            });
         }
      }
   }

   public LiveData<Boolean> getLike() {
      return like;
   }

   public LiveData<String> getTime() {
      return time;
   }

   public LiveData<String> getCountLikeContent() {
      return countLikeContent;
   }

   public LiveData<Integer> getCountLike() {
      return countLike;
   }

   public LiveData<Integer> getCountComment() {
      return countComment;
   }

   public LiveData<Integer> getCountShare() {
      return countShare;
   }

   public void clean() {
      proxyLiveData.removeSource(postModelLiveData);
   }
}
