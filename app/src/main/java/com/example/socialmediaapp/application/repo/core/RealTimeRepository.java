package com.example.socialmediaapp.application.repo.core;

import com.example.socialmediaapp.application.session.RealTimeAccessHandler;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

import java.util.List;
import java.util.Map;

public class RealTimeRepository<T> extends Repository<T> {
  private class IncomingItemListener implements DataEmitListener {
    @Override
    public void onResponse(DataEmit res) {
      String type = res.getType();
      if (!type.equals("update"))
        return;
      Map<String, Object> data = res.getData();
      List<T> l = (List<T>) data.get("items");
      int offset = (int) data.get("offset");
      updateNewItems(offset, l);
    }
  }

  protected RealTimeAccessHandler<T, ?> handler;

  public RealTimeRepository(RealTimeAccessHandler<T, ?> handler) {
    super(handler);
    this.handler = handler;
  }

  @Override
  protected void initEmitListener() {
    super.initEmitListener();
    emitProcessor.addListener(new IncomingItemListener());
  }

  @Override
  public void close() {
    super.close();
    handler.closeSession();
  }

}
