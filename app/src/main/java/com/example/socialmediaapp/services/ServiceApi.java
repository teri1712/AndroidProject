package com.example.socialmediaapp.services;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.AuthenApi;
import com.example.socialmediaapp.apis.MediaApi;
import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.HomeEntranceBody;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.viewmodel.models.HomePageContent;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.MediaPost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.profile.NotMeProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceApi {

    static Retrofit retrofit = ApplicationContainer.getInstance().retrofit;

    static public void loadHomePageContent(Context context, MutableLiveData<HomePageContent> listener) {
        Call<HomeEntranceBody> req = retrofit.create(UserApi.class).getHomePageContent();
        req.enqueue(new Callback<HomeEntranceBody>() {
            @Override
            public void onResponse(Call<HomeEntranceBody> call, Response<HomeEntranceBody> response) {
                if (response.code() == 200) {
                    HomeEntranceBody homeEntranceBody = response.body();
                    listener.postValue(new HomePageContent(homeEntranceBody, context));
                }
            }

            @Override
            public void onFailure(Call<HomeEntranceBody> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    static public Drawable loadImage(Context context, Integer id) {
        if (id == null) return null;

        Drawable drawable = null;
        try {
            Response<ResponseBody> resImg = retrofit.create(MediaApi.class).getImage(id).execute();
            if (resImg.code() == 200) {
                byte[] avt_bmp = resImg.body().bytes();
                drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(avt_bmp, 0, avt_bmp.length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    static public void loadPostsOfUser(Context context, String who, MutableLiveData<List<Post>> listener) {
        Call<List<PostBody>> req = retrofit.create(PostApi.class).loadPostOfUser(who);
        req.enqueue(new Callback<List<PostBody>>() {
            @Override
            public void onResponse(Call<List<PostBody>> call, Response<List<PostBody>> response) {
                if (response.code() != 200) return;
                List<PostBody> posts = response.body();
                List<Post> batch = new ArrayList<>();
                for (PostBody p : posts) {
                    Post item;
                    if (p.getType() == null) {
                        item = new Post(p, context);
                    } else if (p.getType().equals("image")) {
                        item = new ImagePost(p, context);
                    } else {
                        item = new MediaPost(p, context);
                    }
                    batch.add(item);
                }
                listener.postValue(batch);
            }

            @Override
            public void onFailure(Call<List<PostBody>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    static public void loadProfile(Context context, String who, MutableLiveData<UserProfile> listener) {
        Call<UserProfileBody> req = retrofit.create(UserApi.class).getUserProfile(who);
        req.enqueue(new Callback<UserProfileBody>() {
            @Override
            public void onResponse(Call<UserProfileBody> call, Response<UserProfileBody> response) {

                if (response.code() == 200) {
                    UserProfileBody profile = response.body();
                    NotMeProfile user = new NotMeProfile(profile, context);
                    listener.postValue(user);
                }
            }

            @Override
            public void onFailure(Call<UserProfileBody> call, Throwable t) {

            }
        });
    }

    static public void setUpInformation(Context context, String fullname, String alias, String gender, String birthday, Uri avatar, MutableLiveData<String> postSubmitState) {
        RequestBody fullnamePart = RequestBody.create(fullname, MediaType.parse("text/plain"));
        RequestBody aliasPart = RequestBody.create(alias, MediaType.parse("text/plain"));
        RequestBody genderPart = RequestBody.create(gender, MediaType.parse("text/plain"));
        RequestBody birthdayPart = RequestBody.create(birthday, MediaType.parse("text/plain"));
        MultipartBody.Part mediaStreamPart = null;
        if (avatar != null) {
            mediaStreamPart = MultipartBody.Part.createFormData("avatar", "", mediaStreamBody(context.getContentResolver(), avatar));
        }
        Call<ResponseBody> p = retrofit.create(UserApi.class).setUpInfo(fullnamePart, aliasPart, genderPart, birthdayPart, mediaStreamPart);
        p.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final String res = (response.code() == 200) ? "Success" : "Failed";
                postSubmitState.postValue(res);
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                postSubmitState.postValue("Failed");
            }
        });

    }
    static public void performAuthentication(String username, String password, MutableLiveData<String> authenticationState) {
        AuthenApi request = retrofit.create(AuthenApi.class);
        Call<ResponseBody> c = request.login(username, password);
        c.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = (response.code() == 200) ? "Login success" : "Login failed";
                authenticationState.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                authenticationState.postValue("Error occurs");
                t.printStackTrace();
            }
        });
    }

    static public void performSignUp(String username, String password, MutableLiveData<String> authenticationState) {
        AuthenApi request = retrofit.create(AuthenApi.class);
        Call<ResponseBody> c = request.signup(username, password);
        c.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = null;
                try {
                    res = (response.code() == 200) ? "Signup success" : response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    res = "Error occurs";
                }
                System.out.println(response.code());
                authenticationState.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                authenticationState.postValue("Error occurs");
                t.printStackTrace();
            }
        });
    }

    static public void searchForUser(Context context, String query, final MutableLiveData<List<UserBasicInfo>> res) {
        Call<List<UserBasicInfoBody>> req = retrofit.create(UserApi.class).searchForUser(query);
        req.enqueue(new Callback<List<UserBasicInfoBody>>() {
            @Override
            public void onResponse(Call<List<UserBasicInfoBody>> call, Response<List<UserBasicInfoBody>> response) {
                if (response.code() == 200) {
                    List<UserBasicInfoBody> users = response.body();
                    List<UserBasicInfo> batch = new ArrayList<>();
                    for (UserBasicInfoBody u : users) {
                        batch.add(new UserBasicInfo(u, context));
                    }
                    res.postValue(batch);
                } else {
                    res.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<UserBasicInfoBody>> call, Throwable t) {
                res.postValue(null);
                t.printStackTrace();
            }
        });
    }

    static public void onClickOnUser(Context context, final UserBasicInfo who, final MutableLiveData<String> listener) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).onSearchOnUser(who.getAlias());
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = response.code() == 200 ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.postValue("Failed");
            }
        });

    }

    static public void removeRecentProfileItem(Context context, final UserBasicInfo who, final MutableLiveData<String> listener) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).removeProfileRecent(who.getAlias());
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = response.code() == 200 ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.postValue("Failed");
            }
        });

    }

    static public void sendFriendRequest(String who, final MutableLiveData<String> res) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).sendFriendRequest(who);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    res.postValue("Success");
                } else {
                    System.out.println(response.code());
                    res.postValue("Error while handling friend request.");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                res.postValue("Error while handling friend request.");
            }
        });
    }

    static public void acceptFriendRequest(String who, MutableLiveData<String> res) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).acceptFriendRequest(who);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    res.postValue("Success");
                } else {
                    System.out.println(response.code());
                    res.postValue("Error while handling friend request.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                res.postValue("Error while handling friend request.");
            }
        });
    }

    static public void rejectFriendRequest(String who, MutableLiveData<String> res) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).rejectFriendRequest(who);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    res.postValue("Success");
                } else {
                    System.out.println(response.code());
                    res.postValue("Error while handling friend request.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                res.postValue("Error while handling friend request.");
            }
        });
    }
    static public void cancelFriendRequest(String who, MutableLiveData<String> res) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).cancelFriendRequest(who);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    res.postValue("Success");
                } else {
                    System.out.println(response.code());
                    res.postValue("Error while handling friend request.");
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                res.postValue("Error while handling friend request.");
            }
        });

    }



    static public void likeComment(Integer id, MutableLiveData<String> listener) {
        Call<ResponseBody> p = retrofit.create(PostApi.class).userLikeComment(id);
        p.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final String res = (response.code() == 200) ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.postValue("Failed");
                t.printStackTrace();
            }
        });
    }

    static public void unlikeComment(Integer id, MutableLiveData<String> listener) {
        Call<ResponseBody> p = retrofit.create(PostApi.class).userUnlikeComment(id);
        p.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final String res = (response.code() == 200) ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.postValue("Failed");
                t.printStackTrace();
            }
        });
    }

    static public void likeReplyComment(Integer id, MutableLiveData<String> listener) {
        Call<ResponseBody> p = retrofit.create(PostApi.class).userLikeReplyComment(id);
        p.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final String res = (response.code() == 200) ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.postValue("Failed");
                t.printStackTrace();
            }
        });
    }

    static public void unlikeReplyComment(Integer id, MutableLiveData<String> listener) {
        Call<ResponseBody> p = retrofit.create(PostApi.class).userUnlikeReplyComment(id);
        p.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final String res = (response.code() == 200) ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.postValue("Failed");
                t.printStackTrace();
            }
        });
    }

}
