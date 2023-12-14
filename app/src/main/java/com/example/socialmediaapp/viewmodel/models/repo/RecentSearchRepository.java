package com.example.socialmediaapp.viewmodel.models.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import java.util.HashMap;

public class RecentSearchRepository extends Repository<UserBasicInfo> {
    public RecentSearchRepository(RecentSearchAccessHandler dataAccessHandler) {
        super(dataAccessHandler);
    }

    public LiveData<String> deleteItem(String userAlias) {
        MediatorLiveData<String> callBack = new MediatorLiveData<>();
        if (isPolling) {
            Task task = new Task();
            task.callBack = callBack;
            task.action = () -> doDelete(callBack, userAlias);
            qTasks.add(task);
        } else {
            doDelete(callBack, userAlias);
        }
        return callBack;
    }
    private void doDelete(MediatorLiveData<String> callBack, String userAlias) {
        RecentSearchAccessHandler recentSearchAccessHandler = (RecentSearchAccessHandler) dataAccessHandler;
        LiveData<String> res = recentSearchAccessHandler.deleteItem(userAlias);
        callBack.addSource(res, s -> {
            if (!s.equals("Success")) return;
            int pos;
            for (pos = 0; pos < loadedItems.size(); pos++) {
                if (loadedItems.get(pos).getAlias().equals(userAlias)) {
                    break;
                }
            }
            countLoaded--;
            loadedItems.remove(pos);
            HashMap<String, Object> data = new HashMap<>();
            data.put("offset", pos);
            itemUpdate.setValue(new Update(Update.Op.REMOVE, data));
            callBack.setValue(s);
        });
    }
}
