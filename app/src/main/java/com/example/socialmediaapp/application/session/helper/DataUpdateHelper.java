package com.example.socialmediaapp.application.session.helper;

import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataUpdateHelper<T> {
  List<T> update(Map<String, Object> data) throws IOException;

}
