package com.example.socialmediaapp.application.session;
public class UploadTask<T> extends SessionTask {
  protected UploadHelper<T> uploadHelper;

  public UploadTask() {
    super();
  }
  public void setUploadHelper(UploadHelper<T> uploadHelper) {
    this.uploadHelper = uploadHelper;
  }
}
