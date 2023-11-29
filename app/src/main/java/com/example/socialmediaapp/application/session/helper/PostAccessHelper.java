package com.example.socialmediaapp.application.session.helper;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.PostDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.ImagePost;
import com.example.socialmediaapp.application.entity.Post;
import com.example.socialmediaapp.application.entity.UserBasicInfo;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.MediaPost;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostAccessHelper extends DataAccessHelper<com.example.socialmediaapp.viewmodel.models.post.base.Post> {
    private PostDao postDao;
    private UserBasicInfoDao userBasicInfoDao;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
    private Integer ordSeq;
    private AppDatabase db = ApplicationContainer.getInstance().database;

    public PostAccessHelper() {
        super();
        ordSeq = 1;
        postDao = db.getPostDao();
        userBasicInfoDao = db.getUserBasicInfoDao();
    }


    @Override
    public List<com.example.socialmediaapp.viewmodel.models.post.base.Post> tryToFetchFromLocalStorage(Bundle query) {
        int countLoaded = query.getInt("count loaded", 0);
        List<Post> posts = postDao.getPosts(countLoaded, session.getId());
        List<com.example.socialmediaapp.viewmodel.models.post.base.Post> res = new ArrayList<>();
        for (Post p : posts) {
            com.example.socialmediaapp.viewmodel.models.post.base.Post post = null;
            if (Objects.equals("image", p.getType())) {
                com.example.socialmediaapp.viewmodel.models.post.ImagePost imagePost = new com.example.socialmediaapp.viewmodel.models.post.ImagePost();
                post = imagePost;

                imagePost.setImage(BitmapFactory.decodeFile(postDao.findImagePostByPost(p.getId()).getImageUri()));

            } else if (Objects.equals("media", p.getType())) {
                MediaPost mediaPost = new com.example.socialmediaapp.viewmodel.models.post.MediaPost();
                mediaPost.setMediaId(postDao.findMediaPostByPost(p.getId()).getMediaId());
                post = mediaPost;
            } else {
                post = new com.example.socialmediaapp.viewmodel.models.post.base.Post();
            }
            UserBasicInfo u = userBasicInfoDao.findUserBasicInfoById(p.getAuthorId());
            com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
            userBasicInfo.setFullname(u.getFullname());
            userBasicInfo.setAlias(u.getAlias());
            userBasicInfo.setAvatar(BitmapFactory.decodeFile(u.getAvatarUri()));

            post.setAuthor(userBasicInfo);
            post.setId(p.getId());
            post.setCommentCount(p.getCommentCount());
            post.setLikeCount(p.getLikeCount());
            post.setShareCount(p.getShareCount());
            post.setTime(p.getTime());
            post.setType(p.getType());
            post.setStatus(p.getStatus());
            post.setLiked(p.isLiked());

            res.add(post);
        }
        return res;
    }

    @Override
    public Bundle fetchFromServer(Bundle query) throws IOException {
        Bundle result = new Bundle();
        int countRead = query.getInt("read", 0);
        Call<List<PostBody>> req = retrofit.create(PostApi.class).fetchPost(countRead);
        Response<List<PostBody>> res = req.execute();
        List<PostBody> postBodies = res.body();
        List<HashMap<String, Object>> posts = new ArrayList<>();
        for (PostBody postBody : postBodies) {
            HashMap<String, Object> m = dtoConverter.convertToPost(postBody, session.getId());
            posts.add(m);
        }
        db.runInTransaction(new Runnable() {
            @Override
            public void run() {
                ordSeq++;
                for (HashMap<String, Object> m : posts) {
                    Post post = (Post) m.get("post");
                    ImagePost imagePost = (ImagePost) m.get("image post");
                    com.example.socialmediaapp.application.entity.MediaPost mediaPost = (com.example.socialmediaapp.application.entity.MediaPost) m.get("media post");
                    UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");

                    post.setAuthorId((int) userBasicInfoDao.insert(userBasicInfo));
                    postDao.insert(post);
                    post.setOrd(ordSeq);
                    if (imagePost != null) {
                        postDao.insertImagePost(imagePost);
                    }
                    if (mediaPost != null) {
                        postDao.insertMediaPost(mediaPost);
                    }
                }
            }
        });
        result.putInt("count loaded", postBodies.size());
        return result;
    }

    @Override
    public com.example.socialmediaapp.viewmodel.models.post.base.Post uploadToServer(Bundle query) throws IOException {
        String content = query.getString("post content");
        String uriPath = query.getString("media content");
        String type = query.getString("type");
        Uri mediaContent = uriPath == null ? null : Uri.parse(uriPath);

        ContentResolver resolver = ApplicationContainer.getInstance().getContentResolver();
        RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
        RequestBody mediaTypeBody = mediaContent == null ? null : HttpBodyConverter.getTextRequestBody(resolver.getType(mediaContent));
        MultipartBody.Part mediaBody = null;
        Data.Builder res = new Data.Builder();
        mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, resolver, "media_data");

        Call<PostBody> req = null;
        if (type.equals("avatar")) {
            req = retrofit.create(UserApi.class).changeAvatar(contentBody, mediaBody);
        } else if (type.equals("background")) {
            req = retrofit.create(UserApi.class).changeBackground(contentBody, mediaBody);
        } else {
            req = retrofit.create(PostApi.class).upload(contentBody, mediaTypeBody, mediaBody);
        }

        PostBody postBody = req.execute().body();
        return dtoConverter.convertToModelPost(postBody);
    }


    @Override
    public void clean() {
        postDao.deleteAllPost(session.getId());
        userBasicInfoDao.deleteAll(session.getId());
        postDao.deleteAllImagePost(session.getId());
        postDao.deleteAllMediaPost(session.getId());
        ArrayList<String> cached = dtoConverter.getCachedFiles();
        for (String fn : cached) {
            File file = new File(fn);
            file.delete();
        }
    }
}
