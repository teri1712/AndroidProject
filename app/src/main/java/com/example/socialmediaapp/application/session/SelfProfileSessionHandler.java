package com.example.socialmediaapp.application.session;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.apis.entities.UserSessionBody;
import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SelfProfileSessionHandler extends ViewProfileSessionHandler {
    public SelfProfileSessionHandler(String userAlias) {
        super(userAlias);
    }

    public void emitNewAvatarPost(ImagePost imagePost) {
        post(() -> {
            SessionHandler avatarSessionHandler = avatarPostSession.getValue();
            if (avatarSessionHandler != null) {
                avatarSessionHandler.invalidate();
            }
            SessionHandler sh = new PostSessionHandler(imagePost);
            sessionRegistry.bind(sh);
            ((MutableLiveData<SessionHandler>) avatarPostSession).postValue(sh);
        });
    }

    public void emitNewBackgroundPost(ImagePost imagePost) {
        post(() -> {
            SessionHandler backgroundSessionHandler = backgroundPostSession.getValue();
            if (backgroundSessionHandler != null) {
                backgroundSessionHandler.invalidate();
            }
            SessionHandler sh = new PostSessionHandler(imagePost);
            sessionRegistry.bind(sh);
            ((MutableLiveData<SessionHandler>) backgroundPostSession).postValue(sh);
        });
    }

    public MutableLiveData<String> changeInformation(HashMap<String, String> m) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            UpdateUserRequestBody body = new UpdateUserRequestBody();
            body.setFullname(m.get("fullname"));
            body.setAlias(m.get("alias"));
            body.setGender(m.get("gender"));
            body.setBirthday(m.get("birthday"));
            Call<ResponseBody> req = retrofit.create(UserApi.class).changeInfo(body);
            try {
                req.execute();
                UserProfile userProfile = dataSyncEmitter.getValue();
                userProfile.setFullname(m.get("fullname"));
                userProfile.setAlias(m.get("alias"));
                userProfile.setGender(m.get("gender"));
                userProfile.setBirthday(m.get("birthday"));
                dataSyncEmitter.postValue(userProfile);
                callBack.postValue("Success");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");
        }));
        return callBack;
    }

    public MutableLiveData<String> setUpInformation(HashMap<String, String> m) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> {
            RequestBody fullnamePart = HttpBodyConverter.getTextRequestBody(m.get("fullname"));
            RequestBody aliasPart = HttpBodyConverter.getTextRequestBody(m.get("alias"));
            RequestBody genderPart = HttpBodyConverter.getTextRequestBody(m.get("gender"));
            RequestBody birthdayPart = HttpBodyConverter.getTextRequestBody(m.get("birthday"));
            String uriPath = m.get("avatar");
            MultipartBody.Part mediaStreamPart = null;
            if (uriPath != null) {
                try {
                    mediaStreamPart = HttpBodyConverter.getMultipartBody(Uri.parse(uriPath), ApplicationContainer.getInstance().getContentResolver(), "avatar");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Call<UserProfileBody> req = retrofit.create(UserApi.class).setUpInfo(fullnamePart, aliasPart, genderPart, birthdayPart, mediaStreamPart);
            try {
                Response<UserProfileBody> res = req.execute();
                UserProfileBody body = res.body();
                UserProfile profile = dtoConverter.convertToUserProfile(body);

                dataSyncEmitter.postValue(profile);
                callBack.postValue("Success");

                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");
        });
        return callBack;
    }
}
