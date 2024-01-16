package com.example.socialmediaapp.application.repo.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.application.session.ReplyAccessHandler;
import com.example.socialmediaapp.application.session.ReplySessionHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.models.post.ReplyModel;

import java.util.HashMap;

public class ReplyCommentRepository extends RealTimeRepository<HandlerAccess> {
   private MediatorLiveData<Integer> countUnLoadedComment;
   private LiveData<Integer> totalComment;

   public ReplyCommentRepository(ReplyAccessHandler dataAccessHandler) {
      super(dataAccessHandler);
      countUnLoadedComment = new MediatorLiveData<>();
      countUnLoadedComment.setValue(0);
      totalComment = dataAccessHandler.getTotalComment();

      countUnLoadedComment.addSource(totalComment, integer -> {
         countUnLoadedComment.setValue(integer - countLoaded);
      });
   }


   public MediatorLiveData<Integer> getCountUnLoadedComment() {
      return countUnLoadedComment;
   }

   @Override
   protected void setUpdate(@NonNull Update update) {
      super.setUpdate(update);
      countUnLoadedComment.setValue(totalComment.getValue() - countLoaded);
   }

   public void updateNewItems(HandlerAccess t) {
      ReplyModel item = ((ReplySessionHandler) t.access())
              .getReplyData()
              .getValue();
      HandlerAccess lastItem = items.isEmpty()
              ? null
              : items.get(items.size() - 1);
      int lastOrder = lastItem == null
              ? -1 :
              ((ReplySessionHandler) lastItem.access())
                      .getReplyData()
                      .getValue()
                      .getOrder();
      if (item.getOrder() != lastOrder + 1) {
         return;
      }
      items.add(t);
      if (countLoaded == items.size() - 1) {
         countLoaded++;
         HashMap<String, Object> m = new HashMap<>();
         m.put("offset", items.size() - 1);
         m.put("length", 1);
         setUpdate(new Update(Update.Op.ADD, m));
      }
   }

   @Override
   public void close() {
      countUnLoadedComment.removeSource(totalComment);
      super.close();
   }
}
