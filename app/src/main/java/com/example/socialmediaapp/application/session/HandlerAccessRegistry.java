package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.RegistryDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.accesses.AccessRegistry;
import com.example.socialmediaapp.application.entity.accesses.AccessSession;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;

public class HandlerAccessRegistry<T> {
  public static final int CREATE = 0, RESTORE = 1;
  protected SessionHandler sessionHandler;
  private final TreeMap<Integer, AccessSession> accessMap;
  private final ItemInfoProvider itemInfoProvider;
  private AccessRegistry accessRegistry;
  private HandlerStore<T> handlerStore;
  private RegistryDao registryDao;

  public HandlerAccessRegistry(HandlerStore<T> handlerStore, T item, int flag) {
    this.itemInfoProvider = new ItemInfoProvider();
    this.handlerStore = handlerStore;
    this.accessMap = new TreeMap<>();
    this.sessionHandler = new SessionProvider().create(item);
    this.registryDao = DecadeDatabase.getInstance()
            .getRegistryDao();
    if (flag == RESTORE) {
      sessionHandler.sync();
      String alias = itemInfoProvider.getItemAlias(item);
      String itemId = itemInfoProvider.getItemIdentity(item);
      accessRegistry = registryDao.findById(alias, itemId);
      List<AccessSession> clients = registryDao.loadAll(alias, itemId);
      for (AccessSession a : clients) {
        accessMap.put(a.getId(), a);
      }
    } else {
      accessRegistry = new AccessRegistry();
      accessRegistry.setAlias(itemInfoProvider.getItemAlias(item));
      accessRegistry.setItemId(itemInfoProvider.getItemIdentity(item));
      registryDao.insertAccessRegistry(accessRegistry);
    }

    sessionHandler.init();
  }

  public void unBind(Integer accessId) {
    handlerStore.post(() -> {
      AccessSession a = accessMap.remove(accessId);
      assert a != null;
      assert registryDao.deleteAccessSession(a) == 1;

      final boolean end = accessMap.isEmpty();
      if (end) {
        destroy();
      }
    });
  }

  protected HandlerAccess createHandlerAccess(Integer accessId) {
    AccessSession access = accessMap.get(accessId);
    assert access != null;
    HandlerAccess handlerAccess = new HandlerAccess(access);
    handlerAccess.handlerAccessRegistry = this;
    return handlerAccess;
  }

  public HandlerAccess register() {
    AccessSession access = new AccessSession();
    access.setRegistryAlias(accessRegistry.getAlias());
    access.setItemId(accessRegistry.getItemId());

    int accessId = (int) registryDao.insertAccessSession(access);
    access.setId(accessId);
    accessMap.put(accessId, access);

    return createHandlerAccess(accessId);
  }

  protected void destroy() {
    registryDao.deleteAccessRegistry(accessRegistry);
    handlerStore.registryMap.remove(accessRegistry.getItemId());
    sessionHandler.finish();
  }
}