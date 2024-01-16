package com.example.socialmediaapp.application.network;


import com.example.socialmediaapp.application.entity.pend.PendTask;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class SingleTaskRequest extends TaskRequest {

  /* for restoration */
  protected SingleTaskRequest(
          TaskScheduler scheduler,
          PendTask pendTask) throws
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          IllegalAccessException,
          InstantiationException {
    super(scheduler, pendTask);
  }

  protected SingleTaskRequest() {
    super();
  }

  @Override
  protected void postExecuted() {
    super.postExecuted();
    scheduler.onCompleteRequest(reqId);
  }

  public static class Builder extends TaskRequest.Builder {
    public Builder() {
      super();
    }

    @Override
    protected <R extends TaskRequest> R doBuild() {
      return (R) new SingleTaskRequest();
    }
  }
}
