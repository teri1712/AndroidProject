package com.example.socialmediaapp.application.session.helper;

import android.os.Bundle;

import com.example.socialmediaapp.application.session.SessionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class DataAccessHelper<T> {

   protected SessionHandler session;

   public abstract List<T> loadFromLocalStorage(HashMap<String, Object> query);

   public abstract Bundle loadFromServer() throws IOException;

   public abstract T uploadToServer(Bundle query) throws IOException, FileNotFoundException;

   public abstract void cleanAll();

   public void setSession(SessionHandler session) {
      this.session = session;
   }

   public void popRead(T lastItem) {
   }

}
