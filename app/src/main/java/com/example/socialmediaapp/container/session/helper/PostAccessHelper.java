package com.example.socialmediaapp.container.session.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.MediaApi;
import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.converter.DtoConverter;
import com.example.socialmediaapp.container.converter.HttpBodyConverter;
import com.example.socialmediaapp.container.dao.PostDao;
import com.example.socialmediaapp.container.dao.UserBasicInfoDao;
import com.example.socialmediaapp.container.database.AppDatabase;
import com.example.socialmediaapp.container.entity.ImagePost;
import com.example.socialmediaapp.container.entity.Post;
import com.example.socialmediaapp.container.entity.UserBasicInfo;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.Comment;
import com.example.socialmediaapp.viewmodels.models.post.MediaPost;
import com.google.common.util.concurrent.ListenableFuture;

import org.w3c.dom.ls.LSException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostAccessHelper extends DataAccessHelper<com.example.socialmediaapp.viewmodels.models.post.base.Post> {
    private PostDao postDao;
    private UserBasicInfoDao userBasicInfoDao;
    private Context context;
    private WorkManager workManager;

    public PostAccessHelper(Context context) {
        super();
        this.context = context;
        AppDatabase db = ApplicationContainer.getInstance().database;
        postDao = db.getPostDao();
        userBasicInfoDao = db.getUserBasicInfoDao();
        workManager = ApplicationContainer.getInstance().workManager;

    }


    private Drawable loadImage(String uriPath) {
        return new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(uriPath));
    }

    @Override
    public List<com.example.socialmediaapp.viewmodels.models.post.base.Post> tryToFetchFromLocalStorage(Data query) {
        List<Post> posts = postDao.getPosts();
        List<com.example.socialmediaapp.viewmodels.models.post.base.Post> res = new ArrayList<>();
        for (Post p : posts) {
            com.example.socialmediaapp.viewmodels.models.post.base.Post post = null;
            if (Objects.equals("image", p.getType())) {
                com.example.socialmediaapp.viewmodels.models.post.ImagePost imagePost = new com.example.socialmediaapp.viewmodels.models.post.ImagePost();
                post = imagePost;

                imagePost.setImage(loadImage(postDao.findImagePostByPost(p.getId()).getImageUri()));

            } else if (Objects.equals("media", p.getType())) {
                MediaPost mediaPost = new com.example.socialmediaapp.viewmodels.models.post.MediaPost();
                mediaPost.setMediaId(postDao.findMediaPostByPost(p.getId()).getMediaId());
                post = mediaPost;
            } else {
                post = new com.example.socialmediaapp.viewmodels.models.post.base.Post();
            }
            UserBasicInfo u = userBasicInfoDao.findUserBasicInfoById(p.getAuthorId());
            com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo();
            userBasicInfo.setFullname(u.getFullname());
            userBasicInfo.setAlias(u.getAlias());
            userBasicInfo.setAvatar(loadImage(u.getAvatarUri()));

            post.setAuthor(userBasicInfo);
            post.setId(p.getId());
            post.setCommentCount(p.getCommentCount());
            post.setLikeCount(p.getLikeCount());
            post.setShareCount(p.getShareCount());
            post.setTime(p.getTime());
            post.setType(p.getType());
            post.setStatus(p.getStatus());
            post.setLiked(p.isLiked());

        }
        return res;
    }

    @Override
    public ListenableFuture<WorkInfo> fetchFromServer(Data query) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(FetchPostWorker.class).setInputData(query).build();
        workManager.enqueue(req);
        return workManager.getWorkInfoById(req.getId());
    }

    @Override
    public ListenableFuture<WorkInfo> uploadToServer(Data query) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(UploadPostWorker.class).setInputData(query).build();
        workManager.enqueue(req);
        return workManager.getWorkInfoById(req.getId());
    }

    public class FetchPostWorker extends Worker {
        private PostDao postDao;
        private UserBasicInfoDao userBasicInfoDao;
        private DtoConverter dtoConverter;
        private AppDatabase db;
        private Retrofit retrofit;

        public FetchPostWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            db = ApplicationContainer.getInstance().database;
            dtoConverter = new DtoConverter(getApplicationContext());
            postDao = db.getPostDao();
            retrofit = ApplicationContainer.getInstance().retrofit;
            userBasicInfoDao = db.getUserBasicInfoDao();
        }


        @NonNull
        @Override
        public Result doWork() {
            Data query = getInputData();
            Call<List<PostBody>> req = retrofit.create(PostApi.class).fetchPost();
            try {
                Response<List<PostBody>> res = req.execute();
                List<PostBody> postBodies = res.body();
                List<HashMap<String, Object>> posts = new ArrayList<>();
                for (PostBody postBody : postBodies) {
                    HashMap<String, Object> m = dtoConverter.convertToPost(postBody);
                    posts.add(m);
                }
                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {

                        for (HashMap<String, Object> m : posts) {
                            Post post = (Post) m.get("post");
                            ImagePost imagePost = (ImagePost) m.get("image post");
                            com.example.socialmediaapp.container.entity.MediaPost mediaPost = (com.example.socialmediaapp.container.entity.MediaPost) m.get("media post");
                            UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");

                            userBasicInfoDao.insert(userBasicInfo);

                            post.setAuthorId(userBasicInfo.getId());
                            postDao.insert(post);
                            if (imagePost != null) {
                                postDao.insertImagePost(imagePost);
                            }
                            if (mediaPost != null) {
                                postDao.insertMediaPost(mediaPost);
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                return Result.failure();
            }
            return Result.success();
        }
    }


    public class UploadPostWorker extends Worker {

        private PostDao postDao;
        private UserBasicInfoDao userBasicInfoDao;
        private AppDatabase db;
        private DtoConverter dtoConverter;
        private Retrofit retrofit;

        public UploadPostWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            dtoConverter = new DtoConverter(getApplicationContext());
            db = ApplicationContainer.getInstance().database;
            postDao = db.getPostDao();
            userBasicInfoDao = db.getUserBasicInfoDao();
            retrofit = ApplicationContainer.getInstance().retrofit;

        }


        @NonNull
        @Override
        public Result doWork() {
            Data data = getInputData();
            String content = data.getString("post content");
            String uriPath = data.getString("media content");
            Uri mediaContent = uriPath == null ? null : Uri.parse(uriPath);

            ContentResolver resolver = getApplicationContext().getContentResolver();
            RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
            RequestBody mediaTypeBody = mediaContent == null ? null : HttpBodyConverter.getTextRequestBody(resolver.getType(mediaContent));
            MultipartBody.Part mediaBody = null;
            Data.Builder res = new Data.Builder();

            try {
                mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, resolver, "media_data");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return Result.failure(res.putString("error", "file not found").build());
            }

            Call<PostBody> req = retrofit.create(PostApi.class).upload(contentBody, mediaTypeBody, mediaBody);

            try {
                PostBody postBody = req.execute().body();
                HashMap<String, Object> m = dtoConverter.convertToPost(postBody);
                Post p = (Post) m.get("post");

                ImagePost imagePost = (ImagePost) m.get("image post");
                com.example.socialmediaapp.container.entity.MediaPost mediaPost = (com.example.socialmediaapp.container.entity.MediaPost) m.get("media post");
                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
                        userBasicInfoDao.insert(userBasicInfo);
                        p.setAuthorId(userBasicInfo.getId());
                        postDao.insert(p);
                        if (imagePost != null) {
                            postDao.insertImagePost(imagePost);
                        }
                        if (mediaPost != null) {
                            postDao.insertMediaPost(mediaPost);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                return Result.failure(res.putString("error", "fail to make request").build());
            }

            return Result.success();
        }
    }
}
