package com.example.socialmediaapp.application.session;

import android.util.ArrayMap;

import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Real-time update using Long Polling */
/* TODO : For indeed big real-time updates like Ronaldo's new post,
    better to switch to another to prevent in-memory resource intensive in the server side
 */
/* TODO : should have logic : when user first interact with the data
 *   then open this, if not it will wasting polling
 *   i didn't code this */
public abstract class LongPollingHandler<T, B> extends RealTimeAccessHandler<T, B> {
  private Call<List<B>> pollCall;
  /* for re-entrance */
  private int pollingTurn;
  protected boolean longPollingOpened;

  public LongPollingHandler(DataAccessHelper<T> accessHelper, DataUpdateHelper<T> updateHelper) {
    super(accessHelper, updateHelper);
    this.pollingTurn = 0;
    this.longPollingOpened = false;
    initPollCondition();
  }


  private void initPollCondition() {
    Disposable d = networkEmitter.subscribe(next -> {
      String status = next.getString("status");
      assert status != null;
      if (!status.equals("Success")) return;
      int countLoaded = next.getInt("count loaded");
      if (countLoaded == 0 && !longPollingOpened) {
        longPollingOpened = true;
        poll();
      }
    });
  }

  protected abstract Call<List<B>> doPoll();


  private void poll() {
    pollCall = doPoll();
    pollCall.enqueue(new Callback<List<B>>() {
      final int thisPollingTurn = pollingTurn;

      @Override
      public void onResponse(Call<List<B>> call, Response<List<B>> response) {
        if (invalidated || thisPollingTurn != pollingTurn)
          return;
        HttpCallSupporter.debug(response);
        pollCall = null;
        handleResponse(response);
      }

      @Override
      public void onFailure(Call<List<B>> call, Throwable t) {
        if (call.isCanceled()) {

        }
        t.printStackTrace();
      }
    });
  }

  private void handleResponse(Response<List<B>> response) {
    List<B> body = response.body();
    if (body != null) {
      Map<String, Object> data = new ArrayMap<>();
      data.put("items", body);
      updateNewItems(data);
    }
    poll();
  }

  /* call this to cancel the long polling */
  public void closeSession() {
    ensureThread(() -> {
      if (pollCall != null) {
        pollCall.cancel();
        pollCall = null;
      }
      ++pollingTurn;
    });
  }

  @Override
  protected void invalidate() {
    if (pollCall != null) {
      pollCall.cancel();
    }
    super.invalidate();
  }
}
