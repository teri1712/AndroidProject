package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.application.entity.accesses.AccessSession;

public class HandlerAccess {
   private AccessSession accessSession;
   protected HandlerAccessRegistry handlerAccessRegistry;

   protected HandlerAccess(AccessSession accessSession) {
      this.accessSession = accessSession;
   }

   public int getId() {
      return accessSession.getId();
   }

   public String getItemId() {
      return accessSession.getItemId();
   }

   protected void release() {
      handlerAccessRegistry.unBind(accessSession.getId());
   }

   public <T extends SessionHandler> T access() {
      return (T) handlerAccessRegistry.sessionHandler;
   }
}