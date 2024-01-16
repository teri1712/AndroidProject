package com.example.socialmediaapp.application.repo.preference;

import android.os.Bundle;

public class DefaultPreference<T> extends FetchPreference<T> {

   public DefaultPreference(T lastItem) {
      super(lastItem);
   }

   @Override
   protected void apply(Bundle prefs) {

   }
}
