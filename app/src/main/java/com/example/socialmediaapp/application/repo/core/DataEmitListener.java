package com.example.socialmediaapp.application.repo.core;

import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

public interface DataEmitListener {
  void onResponse(DataEmit res);
}
