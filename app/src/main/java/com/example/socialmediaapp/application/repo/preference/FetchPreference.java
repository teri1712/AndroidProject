package com.example.socialmediaapp.application.repo.preference;

import android.os.Bundle;

public abstract class FetchPreference<T> {
   private Bundle prefs;
   private T lastItem;

   public FetchPreference(T lastItem) {
      this.lastItem = lastItem;
      prefs = new Bundle();
      apply(prefs);
   }

   protected abstract void apply(Bundle prefs);

   public T getLastItem() {
      return lastItem;
   }

   public void setLastItem(T lastItem) {
      this.lastItem = lastItem;
   }

   public Bundle getPrefs() {
      return prefs;
   }
}
