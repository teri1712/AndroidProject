package com.example.socialmediaapp.application.repo.core;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.session.RealTimeAccessHandler;


public class CommentRepository extends RealTimeRepository<HandlerAccess> {
  /* Replacement for Long Polling
   * Typically happens when there a lot of update incoming
   * like Ronaldo New Post's comments */

  /* TODO : Not implemented the underlying handler yet*/
  private class HintCommentListener implements DataEmitListener {
    @Override
    public void onResponse(DataEmit res) {
      String type = res.getType();
      if (type.equals("hint")) {
        setUpdate(new Update(Update.Op.HINT_UPDATE, null));
      }
    }
  }

  public CommentRepository(RealTimeAccessHandler<HandlerAccess, ?> handler) {
    super(handler);
  }

  @Override
  protected void init() {
    super.init();
    emitProcessor.addListener(new HintCommentListener());
  }

  /* NOTE : This method when clicking on Comment Notification then scrolling to the target comment
   * TODO : Implement the Preference-Read, another way to sort a list of comment
   *  then making the scroll faster, and also can apply additional filters,
   *  by default the Prefs is owner's comment first then others */
  private void pollUpTo(
          MediatorLiveData<Integer> callBack,
          Comparable<HandlerAccess> comparable) {
    final int index = countLoaded;
    LiveData<String> poll = fetchNewItem(10);
    callBack.addSource(poll, s -> {
      callBack.removeSource(poll);
      if (index == countLoaded) {
        callBack.setValue(null);
        return;
      }
      for (int i = index; i < countLoaded; i++) {
        if (comparable.compareTo(items.get(i)) == 0) {
          callBack.setValue(i);
          return;
        }
      }
      pollUpTo(callBack, comparable);
    });
  }

  public LiveData<Integer> loadUpTo(Comparable<HandlerAccess> cmp) {
    for (int i = 0; i < countLoaded; i++) {
      if (cmp.compareTo(items.get(i)) == 0) {
        return new MutableLiveData<>(i);
      }
    }
    MediatorLiveData<Integer> callBack = new MediatorLiveData<>();
    pollUpTo(callBack, cmp);
    return callBack;
  }
}
