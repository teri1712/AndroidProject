package com.example.socialmediaapp.home.fragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class SimpleLifecycleOwner implements LifecycleOwner {

   private LifecycleRegistry registry;

   public SimpleLifecycleOwner() {
      registry = new LifecycleRegistry(this);
   }

   public void resume() {
      registry.setCurrentState(Lifecycle.State.RESUMED);
   }

   /* make to created so the atLeast(RESUMED) will return false
    which make the observer become inactive*/
   public void pause() {
      registry.setCurrentState(Lifecycle.State.CREATED);
   }

   public void destroy() {

      registry.setCurrentState(Lifecycle.State.DESTROYED);
   }

   @NonNull
   @Override
   public Lifecycle getLifecycle() {
      return registry;
   }
}
