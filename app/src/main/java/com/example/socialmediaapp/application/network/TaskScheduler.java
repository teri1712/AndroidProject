package com.example.socialmediaapp.application.network;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;
import androidx.work.impl.WorkManagerImpl;
import androidx.work.impl.utils.taskexecutor.WorkManagerTaskExecutor;

import com.example.socialmediaapp.application.dao.pend.PendDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.pend.PendRequest;
import com.example.socialmediaapp.application.entity.pend.PendTask;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/* inspired from WorkManager */
public class TaskScheduler implements Scheduler {
  private static TaskScheduler scheduler;

  public static TaskScheduler getInstance() {
    return scheduler;
  }

  static {
    scheduler = new TaskScheduler();
    scheduler.init();
  }


  private static final String SCHEDULER_LOOPER_THREAD = "UWUWUWUWUWUWU";
  protected final Map<UUID, Map<String, TaskRequest>> requestMap;
  private final Map<UUID, AutoPolling> serialMap;
  private final Set<UUID> persistSet;
  private final Executor executor;
  private final Handler handler;
  private final DecadeDatabase db;
  private final PendDao dao;

  private TaskScheduler() {
    this.serialMap = new HashMap<>();
    this.requestMap = new HashMap<>();
    this.persistSet = new HashSet<>();
    this.handler = new Handler(new HandlerThread(SCHEDULER_LOOPER_THREAD).getLooper());
    this.executor = Executors.newFixedThreadPool(4);
    this.db = DecadeDatabase.getInstance();
    this.dao = db.getPendDao();
  }

  public void init() {
    List<PendRequest> pends = dao.findAll();
    for (PendRequest req : pends) {
      List<PendTask> tasks = dao.findAllByRequest(req.getId());
      if (tasks.isEmpty()) {
        /* orphan when system suddenly destroy app */
        dao.deleteRequest(req.getId());
        continue;
      }
      UUID id = UUID.fromString(req.getId());
      Map<String, TaskRequest> pendTask = new HashMap<>();
      requestMap.put(id, pendTask);
      boolean serial = req.getType().equals("Serial");
      for (PendTask pend : tasks) {
        if (serial) {
          try {
            TaskRequest request = new SerialTaskRequest(this, pend);
            pendTask.put(pend.getId(), request);
            submitSerial(id, request);
          } catch (ClassNotFoundException
                   | InvocationTargetException
                   | NoSuchMethodException
                   | InstantiationException
                   | IllegalAccessException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  @Override
  public void enqueue(TaskRequest taskRequest) {
    handler.post(() -> {
      schedule(taskRequest);
    });
  }

  private void putNewTask(UUID reqId, TaskRequest request) {
    Map<String, TaskRequest> pendTasks = requestMap.get(reqId);
    if (pendTasks == null) {
      pendTasks = new HashMap<>();
    }
    pendTasks.put(request.pendTask.getId(), request);
  }

  private void schedule(TaskRequest task) {
    boolean isSerial = task instanceof SerialTaskRequest;
    TaskDetails details = task.details;
    UUID reqId = isSerial
            ? UUID.nameUUIDFromBytes(((SerialTaskRequest) task).getAlias().getBytes())
            : UUID.randomUUID();
    PendRequest req = new PendRequest();
    req.setId(reqId.toString());
    req.setType(isSerial ? "Serial" : "Normal");

    task.init(this, reqId);
    synchronized (requestMap) {
      putNewTask(reqId, task);
      if (details.willRestore) {
        if (!persistSet.contains(reqId)) {
          persistSet.add(reqId);
          dao.insertRequest(req);
        }
        task.initInLocal();
      }
      if (task instanceof SingleTaskRequest) {
        submit(task::execute);
      }
      if (task instanceof SerialTaskRequest) {
        submitSerial(reqId, task);
      }
    }
  }

  private void submitSerial(
          UUID id,
          TaskRequest task) {
    AutoPolling queue = serialMap.get(id);
    if (queue == null) {
      queue = new AutoPolling(id, this);
      serialMap.put(id, queue);
    }
    queue.push(task);
  }

  protected void submit(Runnable runnable) {
    executor.execute(runnable);
  }

  /* NOTE : has the lock monitor
   * called by AutoPolling */
  protected void onSerialRequestComplete(UUID reqId) {
    onCompleteRequest(reqId);
    serialMap.remove(reqId);
  }

  protected void onCompleteRequest(UUID reqId) {
    synchronized (requestMap) {
      requestMap.remove(reqId);
      if (persistSet.remove(reqId)) {
        dao.deleteRequest(reqId.toString());
      }
    }
  }

  protected void onTaskInProgress(UUID reqId, String taskId) {
    synchronized (requestMap) {
      Map<String, TaskRequest> pendTaskMap = requestMap.get(reqId);
      assert pendTaskMap != null;
    }
  }

  protected void onTaskComplete(UUID reqId, String taskId) {
    synchronized (requestMap) {
      Map<String, TaskRequest> pendTaskMap = requestMap.get(reqId);
      assert pendTaskMap != null;
      TaskRequest monitor = pendTaskMap.remove(taskId);
      assert monitor != null;
    }
  }
//
//  public LiveData<List<PendMonitor>> findPendTasksMonitor(UUID reqId) {
//    MutableLiveData<List<PendMonitor>> monitors = new MutableLiveData<>();
//    submit(new Runnable() {
//      @Override
//      public void run() {
//        List<PendMonitor> list = null;
//        synchronized (requestMap) {
//          Map<String, PendMonitor> pendTask = requestMap.get(reqId);
//          if (pendTask != null) {
//            list = new ArrayList<>(pendTask.values());
//          }
//        }
//        monitors.postValue(list);
//      }
//    });
//    return monitors;
//  }
}
