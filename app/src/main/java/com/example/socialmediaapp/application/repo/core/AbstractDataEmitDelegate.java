package com.example.socialmediaapp.application.repo.core;

import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDataEmitDelegate implements DataEmitListener {
  private List<DataEmitListener> emitListeners;

  public AbstractDataEmitDelegate() {
    emitListeners = new ArrayList<>();
  }

  @Override
  public void onResponse(DataEmit res) {
    if (!willNotProcess(res)) {
      delegate(res);
    }
  }

  public void addListener(DataEmitListener listener) {
    emitListeners.add(listener);
  }
  public void removeListener(DataEmitListener listener) {
    emitListeners.remove(listener);
  }

  protected abstract boolean willNotProcess(DataEmit res);

  private void delegate(DataEmit res) {
    // safe iterate
    List<DataEmitListener> lis = new ArrayList<>(emitListeners);
    for (DataEmitListener e : lis) {
      e.onResponse(res);
    }
  }
}
