package com.example.socialmediaapp.application.repo.core;

import android.util.ArrayMap;

import com.example.socialmediaapp.application.repo.core.tasks.FutureResult;
import com.example.socialmediaapp.application.repo.core.tasks.Task;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;
import com.example.socialmediaapp.application.repo.core.utilities.Update;

import java.util.Map;

public class AccumulateTask extends Task<String> {
  private int offset;
  private int length;
  private int prog;
  private Repository<?> repo;

  public AccumulateTask(
          Repository<?> repo,
          int offset,
          int length) {
    this.repo = repo;
    this.offset = offset;
    this.length = length;
    this.prog = 0;
  }

  private FutureResult<String> pollItems() {
    FutureResult<String> future = new FutureResult<>();
    Repository<?>.DataEmitProcessor emitter = repo.emitProcessor;
    DataEmitListener listener = new DataEmitListener() {
      @Override
      public void onResponse(DataEmit res) {
        String type = res.getType();
        if (!type.equals("fetch")) return;
        emitter.removeListener(this);
        int l = Math.min(length - prog, repo.length() - (offset + prog));
        prog += l;
        if (prog == length || l == 0) {
          Map<String, Object> data = new ArrayMap<>();
          data.put("offset", offset);
          data.put("length", prog);
          repo.consume(prog);
          repo.setUpdate(new Update(Update.Op.ADD, data));
          future.onComplete("Success");
          return;
        }
        pollItems();
      }
    };
    emitter.addListener(listener);
    repo.fetchFromDataLayer();
    return future;
  }

  @Override
  protected FutureResult<String> onDoTask() {
    return pollItems();
  }
}
