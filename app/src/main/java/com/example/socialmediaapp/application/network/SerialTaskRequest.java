package com.example.socialmediaapp.application.network;


import com.example.socialmediaapp.application.entity.pend.PendTask;

import java.lang.reflect.InvocationTargetException;

public class SerialTaskRequest extends TaskRequest {
  private String alias;

  /* for restoration */
  protected SerialTaskRequest(
          TaskScheduler scheduler,
          PendTask pendTask) throws
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          IllegalAccessException,
          InstantiationException {
    super(scheduler, pendTask);
    this.alias = task.getData().getString("alias");
  }

  protected SerialTaskRequest(String alias) {
    super();
    this.alias = alias;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public static class Builder extends TaskRequest.Builder {
    private String alias;

    public Builder() {
      super();
    }

    @Override
    protected <R extends TaskRequest> R doBuild() {
      assert alias != null;
      details.data.putString("alias", alias);
      TaskRequest request = new SerialTaskRequest(alias);
      return (R) request;
    }

    public Builder setAlias(String alias) {
      this.alias = alias;
      return this;
    }
  }
}
