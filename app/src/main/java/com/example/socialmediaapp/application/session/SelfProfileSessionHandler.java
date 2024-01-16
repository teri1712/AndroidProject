package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.UserApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.api.entities.UserProfileBody;
import com.example.socialmediaapp.api.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.user.ProfileDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.user.UserProfile;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.TaskRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SelfProfileSessionHandler extends ProfileSessionHandler {
  private final ProfileDao dao;

  public SelfProfileSessionHandler(UserProfileProvider profileProvider, UserProfile userProfile) {
    super(profileProvider, userProfile);
    this.dao = DecadeDatabase.getInstance().getProfileDao();
  }

  protected void onNewAvatarPost(Map<String, Object> itemPack) {
    Post item = (Post) itemPack.get("post");
    HandlerAccess access = PostHandlerStore.getInstance().register(item, itemPack);
    profile.setAvatarPostAccessId(access.getId());
    profile.setAvatarPostId(item.getId());
    dao.update(profile);

    avtPostAccess.postValue(access);
  }

  protected void onNewBackgroundPost(Map<String, Object> itemPack) {
    Post item = (Post) itemPack.get("post");
    HandlerAccess access = PostHandlerStore.getInstance().register(item, itemPack);
    profile.setBackgroundPostAccessId(access.getId());
    profile.setBackgroundPostId(item.getId());
    dao.update(profile);

    avtPostAccess.postValue(access);
  }

  private TaskRequest.Builder createSerialTask(
          Class<? extends SelfProfileTask> clazz,
          MutableLiveData<?> callBack) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    SelfProfileTask task = builder.fromTask(clazz);
    task.setHandler(this);
    task.setCallBack(callBack);
    return builder.setAlias("Profile" + profile.getId()).setWillRestore(true);
  }

  public LiveData<String> changeInformation(final Bundle data) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    TaskRequest.Builder builder = createSerialTask(UpdateInformationTask.class, callBack);
    TaskRequest request = builder.setData(data).build();
    postTask(request);
    return callBack;
  }

  public LiveData<String> setUpInformation(final Bundle data) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    TaskRequest.Builder builder = createSerialTask(SetUpInformationTask.class, callBack);
    TaskRequest request = builder.setData(data).build();
    postTask(request);
    return callBack;
  }

  public LiveData<HandlerAccess> changeAvatar(Bundle data) {
    MutableLiveData<HandlerAccess> callBack = new MutableLiveData<>();
    data.putString("action", "avatar");
    data.putString("user id", profile.getId());
    TaskRequest request = createSerialTask(UpdateMediaPostTask.class, callBack)
            .setData(data).build();
    postTask(request);
    return callBack;
  }

  public LiveData<HandlerAccess> changeBackground(Bundle data) {
    MutableLiveData<HandlerAccess> callBack = new MutableLiveData<>();
    data.putString("action", "background");
    data.putString("user id", profile.getId());
    TaskRequest request = createSerialTask(UpdateMediaPostTask.class, callBack)
            .setData(data).build();
    postTask(request);
    return callBack;
  }

  public static class SelfProfileTask<T> extends SessionTask {
    protected SelfProfileSessionHandler profileHandler;
    protected MutableLiveData<T> callBack;
    protected ProfileDao dao;

    public SelfProfileTask() {
      dao = DecadeDatabase.getInstance().getProfileDao();
    }

    @Override
    public void setHandler(SessionHandler handler) {
      super.setHandler(handler);
      profileHandler = (SelfProfileSessionHandler) handler;
    }

    public void setCallBack(MutableLiveData<T> callBack) {
      this.callBack = callBack;
    }
  }

  public static class UpdateInformationTask extends SelfProfileTask<String> {
    private void updateInLocal(Bundle data) {
      UserProfile profile = null;
      if (handler != null) {
        profile = profileHandler.profile;
      } else {
        profile = dao.findById(data.getString("user id"));
      }
      profile.setFullname(data.getString("fullname"));
      profile.setGender(data.getString("gender"));
      profile.setBirthday(data.getString("birthday"));
      profile.setAlias(data.getString("alias"));
      dao.update(profile);
      if (handler != null) {
        profileHandler.profileLivedata.postValue(ModelConvertor.convertToUserProfileModel(profile));
      }
    }

    @Override
    public void doTask() {
      Bundle data = getData();
      UpdateUserRequestBody body = new UpdateUserRequestBody();
      body.setFullname(data.getString("fullname"));
      body.setAlias(data.getString("alias"));
      body.setGender(data.getString("gender"));
      body.setBirthday(data.getString("birthday"));
      Call<ResponseBody> req = HttpCallSupporter.create(UserApi.class).changeInfo(body);
      try {
        req.execute();
        updateInLocal(data);
        callBack.postValue("Success");
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
      callBack.postValue("Failed");
    }

  }

  public static class SetUpInformationTask extends SelfProfileTask<String> {

    @Override
    public void doTask() {
      Bundle data = getData();
      RequestBody fullnamePart = HttpBodyConverter.getTextRequestBody(data.getString("fullname"));
      RequestBody aliasPart = HttpBodyConverter.getTextRequestBody(data.getString("alias"));
      RequestBody genderPart = HttpBodyConverter.getTextRequestBody(data.getString("gender"));
      RequestBody birthdayPart = HttpBodyConverter.getTextRequestBody(data.getString("birthday"));
      String uriPath = data.getString("avatar");
      MultipartBody.Part mediaStreamPart = null;
      if (uriPath != null) {
        ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();
        Uri uri = Uri.parse(uriPath);
        try {
          mediaStreamPart = HttpBodyConverter.getMultipartBody(uri, resolver, "avatar");
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
      Call<UserProfileBody> req = HttpCallSupporter.create(UserApi.class).setUpInfo(fullnamePart, aliasPart, genderPart, birthdayPart, mediaStreamPart);
      try {
        Response<UserProfileBody> res = req.execute();
        UserProfileBody body = res.body();
        UserProfile profile = DtoConverter.convertToUserProfile(body);
        dao.update(profile);
        if (profileHandler != null) {
          profileHandler.profile = profile;
          profileHandler.profileLivedata.postValue(ModelConvertor.convertToUserProfileModel(profile));
        }
        callBack.postValue("Success");
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
      callBack.postValue("Failed");
    }
  }

  public static class UpdateMediaPostTask extends SelfProfileTask<HandlerAccess> {
    @Override
    public void doTask() {
      ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();
      Bundle data = getData();
      String action = data.getString("action");
      String userId = data.getString("user id");
      HandlerAccess access = null;
      switch (action) {
        case "avatar": {
          try {
            String content = data.getString("post content");
            String uriPath = data.getString("media content");
            Uri mediaContent = uriPath == null ? null : Uri.parse(uriPath);

            RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
            MultipartBody.Part mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, resolver, "media_data");
            Call<PostBody> req = HttpCallSupporter.create(UserApi.class).changeAvatar(contentBody, mediaBody);
            Response<PostBody> res = req.execute();
            PostBody post = res.body();
            Map<String, Object> itemPack = DtoConverter.convertToPost(post);
            if (profileHandler != null) {
              profileHandler.onNewAvatarPost(itemPack);
            } else {
              Post item = (Post) itemPack.get("post");
              access = PostHandlerStore.getInstance().register(item, itemPack);
              UserProfile profile = dao.findById(userId);
              profile.setAvatarPostAccessId(access.getId());
              profile.setAvatarPostId(item.getId());
              dao.update(profile);
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        }
        case "background": {
          try {

            String content = data.getString("post content");
            String uriPath = data.getString("media content");
            Uri mediaContent = uriPath == null ? null : Uri.parse(uriPath);

            RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
            MultipartBody.Part mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, resolver, "media_data");
            Call<PostBody> req = HttpCallSupporter.create(UserApi.class).changeBackground(contentBody, mediaBody);
            Response<PostBody> res = req.execute();
            PostBody post = res.body();

            Map<String, Object> itemPack = DtoConverter.convertToPost(post);
            if (profileHandler != null) {
              profileHandler.onNewBackgroundPost(itemPack);
            } else {
              Post item = (Post) itemPack.get("post");
              access = PostHandlerStore.getInstance().register(item, itemPack);
              UserProfile profile = dao.findById(userId);
              profile.setBackgroundPostAccessId(access.getId());
              profile.setBackgroundPostId(item.getId());
              dao.update(profile);
            }
          } catch (IOException e) {
            e.printStackTrace();
            callBack.postValue(null);
          }
          break;
        }
      }
      if (callBack != null) {
        callBack.postValue(access);
      }
    }
  }
}
