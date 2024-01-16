package com.example.socialmediaapp.application.session;


public class DeferredValue<T> {
   private T value;

   public DeferredValue() {
   }

   public synchronized T get() {
      try {
         if (value == null) {
            wait();
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      return value;
   }

   public synchronized void set(T value) {
      this.value = value;
      notifyAll();
   }

}