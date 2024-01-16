package com.example.socialmediaapp.application.session;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.database.DecadeDatabase;

import java.util.Map;
import java.util.TreeMap;

/* Make every item a singleton */
public abstract class HandlerStore<T> {
  protected final DecadeDatabase db;
  protected final Map<String, HandlerAccessRegistry<T>> registryMap;
  protected final ItemInfoProvider itemInfoProvider;

  protected abstract void cleanOrphanOnCreate();

  protected abstract void createInLocal(Map<String, Object> itemPack);

  protected abstract T loadFromLocal(String itemId);

  protected HandlerStore() {
    db = DecadeDatabase.getInstance();
    registryMap = new TreeMap<>();
    itemInfoProvider = new ItemInfoProvider();
    init();
    cleanOrphanOnCreate();
  }

  protected void init() {
  }

  public synchronized HandlerAccess register(T item, Map<String, Object> payload) {
    String itemId = itemInfoProvider.getItemIdentity(item);
    HandlerAccessRegistry<T> registry = getRegistry(itemId);
    if (registry == null) {
      createInLocal(payload);
      registry = createRegistry(item, HandlerAccessRegistry.CREATE);
    }
    return registry.register();
  }

  public synchronized HandlerAccess getHandlerAccess(String itemId, int accessId) {
    HandlerAccessRegistry<T> registry = registryMap.get(itemId);
    return registry.createHandlerAccess(accessId);
  }

  public LiveData<HandlerAccess> findHandlerAccess(String itemId, Integer accessId) {
    MutableLiveData<HandlerAccess> callBack = new MutableLiveData<>();
    DecadeApplication.getInstance().mainHandler.post(() -> post(() -> {
      HandlerAccessRegistry<T> registry = getRegistry(itemId);
      assert registry != null;
      callBack.postValue(registry.createHandlerAccess(accessId));
    }));
    return callBack;
  }

  private HandlerAccessRegistry<T> createRegistry(T t, int flag) {
    HandlerAccessRegistry<T> registry = new HandlerAccessRegistry<>(this, t, flag);
    registryMap.put(itemInfoProvider.getItemIdentity(t), registry);

    return registry;
  }

  protected HandlerAccessRegistry<T> getRegistry(String itemId) {
    HandlerAccessRegistry<T> registry = registryMap.get(itemId);
    if (registry == null) {
      T item = loadFromLocal(itemId);
      if (item == null) return null;
      registry = createRegistry(item, HandlerAccessRegistry.RESTORE);
    }
    return registry;
  }

  protected synchronized void post(Runnable action) {
    action.run();
  }
}
