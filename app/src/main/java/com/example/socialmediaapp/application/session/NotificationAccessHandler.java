package com.example.socialmediaapp.application.session;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.entities.NotificationBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.NotificationModel;
import com.example.socialmediaapp.models.NotifyDetailsModel;

import java.io.IOException;
import java.util.Map;

public class NotificationAccessHandler
        extends FcmAccessHandler<NotificationModel, NotificationBody> {
  private final Handler mainThread;
  private NotififyAccessHelper accessHelper;
  private MutableLiveData<NotifyDetailsModel> notifyDetails;

  public NotificationAccessHandler(NotififyAccessHelper accessHelper) {
    super(accessHelper, accessHelper);
    mainThread = new Handler(Looper.getMainLooper());
  }

  @Override
  protected void init() {
    super.init();
    this.accessHelper = (NotififyAccessHelper) dataAccessHelper;
    notifyDetails = new MutableLiveData<>();
    TaskRequest request = createSerialTask(new Runnable() {
      @Override
      public void run() {
        try {
          accessHelper.initAndLoadPreset();
          updateToUiLayer(accessHelper.notifyDetails.getCntUnRead());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    postTask(request);
  }
  private void updateToUiLayer(final int cntUnRead) {
    mainThread.post(new Runnable() {
      @Override
      public void run() {
        NotifyDetailsModel model = notifyDetails.getValue();
        model.setCountUnRead(cntUnRead);
        notifyDetails.postValue(model);
      }
    });
  }

  public MutableLiveData<NotifyDetailsModel> getNotifyDetails() {
    return notifyDetails;
  }

  @Override
  protected void onUpdateCompleted(Map<String, Object> data) {
    super.onUpdateCompleted(data);
    DecadeApplication.getInstance()
            .onlineSessionHandler
            .getUserHandler()
            .onNewNotification(data);
    int cntUnRead = (int) data.get("count un read");
    updateToUiLayer(cntUnRead);
  }

  @Override
  protected void postUpdateProcessed(Map<String, Object> data) {
    super.postUpdateProcessed(data);
  }

  public void doRead() {
    NotifyDetailsModel model = notifyDetails.getValue();
    model.setCountUnRead(0);
    TaskRequest request = createSerialTask(new Runnable() {
      @Override
      public void run() {
        try {
          accessHelper.updateRead();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    postTask(request);
  }

}
