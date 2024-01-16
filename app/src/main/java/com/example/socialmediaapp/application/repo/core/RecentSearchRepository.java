package com.example.socialmediaapp.application.repo.core;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.util.HashMap;

public class RecentSearchRepository extends Repository<UserBasicInfoModel> {
   private RecentSearchAccessHandler recentHandler;

   public RecentSearchRepository(RecentSearchAccessHandler recentHandler) {
      super(recentHandler);
      this.recentHandler = recentHandler;
   }

   public LiveData<String> deleteItem(String id) {
      MediatorLiveData<String> callBack = new MediatorLiveData<>();
      Task task = new Task(callBack);
      task.action = () -> doDelete(callBack, id);
      submit(task);
      return task.ml;
   }

   private void doDelete(MediatorLiveData<String> callBack, String userId) {
      LiveData<String> res = recentHandler.deleteItem(userId);
      callBack.addSource(res, s -> {
         if (!s.equals("Success")) return;
         int pos;
         for (pos = 0; pos < items.size(); pos++) {
            if (items.get(pos).getId().equals(userId)) {
               break;
            }
         }
         countLoaded--;
         items.remove(pos);
         HashMap<String, Object> data = new HashMap<>();
         data.put("offset", pos);
         setUpdate(new Update(Update.Op.REMOVE, data));
         callBack.setValue(s);
      });
   }

   @Override
   protected void doUpload(MediatorLiveData<String> callBack, Bundle data) {
      callBack.addSource(recentHandler.uploadNewItem(data), new Observer<HashMap<String, Object>>() {
         @Override
         public void onChanged(HashMap<String, Object> hashMap) {
            String status = (String) hashMap.get("status");
            String id = data.getString("user id");
            if (status.equals("Success")) {
               for (int i = 0; i < items.size(); i++) {
                  UserBasicInfoModel u = items.get(i);
                  if (id.equals(u.getId())) {
                     items.remove(i);
                     HashMap<String, Object> m = new HashMap<>();
                     m.put("offset", i);
                     setUpdate(new Update(Update.Op.REMOVE, m));
                     break;
                  }
               }
               UserBasicInfoModel item = (UserBasicInfoModel) hashMap.get("item");
               items.add(0, item);
               countLoaded++;
               HashMap<String, Object> m = new HashMap<>();
               m.put("offset", 0);
               m.put("length", 1);
               setUpdate(new Update(Update.Op.ADD, m));
            }
            callBack.setValue(status);
         }
      });
   }
}
