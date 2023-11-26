package com.example.socialmediaapp.container.session.helper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.converter.DtoConverter;
import com.example.socialmediaapp.container.converter.HttpBodyConverter;
import com.example.socialmediaapp.container.dao.CommentDao;
import com.example.socialmediaapp.container.dao.UserBasicInfoDao;
import com.example.socialmediaapp.container.database.AppDatabase;
import com.example.socialmediaapp.container.entity.Comment;
import com.example.socialmediaapp.container.entity.UserBasicInfo;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentAccessHelper extends DataAccessHelper<com.example.socialmediaapp.viewmodel.models.post.Comment> {
    private CommentDao commentDao;
    private UserBasicInfoDao userBasicInfoDao;
    private Integer postId;
    private WorkManager workManager;

    public CommentAccessHelper(Integer postId) {
        super();
        this.postId = postId;
        AppDatabase db = ApplicationContainer.getInstance().database;
        commentDao = db.getCommentDao();
        userBasicInfoDao = db.getUserBasicInfoDao();
        workManager = ApplicationContainer.getInstance().workManager;
    }


    @Override
    public List<com.example.socialmediaapp.viewmodel.models.post.Comment> tryToFetchFromLocalStorage(Data query) {
        List<Comment> comments = commentDao.getComments();
        List<com.example.socialmediaapp.viewmodel.models.post.Comment> res = new ArrayList<>();
        for (Comment c : comments) {
            com.example.socialmediaapp.viewmodel.models.post.Comment comment = new com.example.socialmediaapp.viewmodel.models.post.Comment();
            comment.setId(c.getId());
            comment.setContent(c.getContent());
            comment.setLiked(c.isLiked());
            comment.setTime(c.getTime());
            comment.setCountLike(c.getLikeCount());
            UserBasicInfo u = userBasicInfoDao.findUserBasicInfoById(c.getAuthorId());
            com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
            userBasicInfo.setFullname(u.getFullname());
            userBasicInfo.setAlias(u.getAlias());
            userBasicInfo.setAvatar(BitmapFactory.decodeFile(u.getAvatarUri()));
            comment.setAuthor(userBasicInfo);
        }
        return res;
    }

    @Override
    public ListenableFuture<WorkInfo> fetchFromServer() {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(FetchCommentWorker.class).build();
        workManager.enqueue(req);
        return workManager.getWorkInfoById(req.getId());
    }

    @Override
    public ListenableFuture<WorkInfo> uploadToServer(Data query) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(UploadCommentWorker.class).setInputData(query).build();
        workManager.enqueue(req);
        return workManager.getWorkInfoById(req.getId());
    }

    public class FetchCommentWorker extends Worker {
        private UserBasicInfoDao userBasicInfoDao;
        private DtoConverter dtoConverter;
        private AppDatabase db;
        private Retrofit retrofit;

        public FetchCommentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            db = ApplicationContainer.getInstance().database;
            dtoConverter = new DtoConverter(getApplicationContext());
            retrofit = ApplicationContainer.getInstance().retrofit;
            userBasicInfoDao = db.getUserBasicInfoDao();
        }


        @NonNull
        @Override
        public Result doWork() {
            Data query = getInputData();
            Call<List<CommentBody>> req = retrofit.create(PostApi.class).fetchComments(query.getInt("post id", -1));
            try {
                Response<List<CommentBody>> res = req.execute();
                List<CommentBody> commentBodies = res.body();
                final List<HashMap<String, Object>> comments = new ArrayList<>();
                for (CommentBody c : commentBodies) {
                    comments.add(dtoConverter.convertToComment(c));
                }
                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        for (HashMap<String, Object> m : comments) {
                            Comment comment = (Comment) m.get("comment");
                            UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
                            userBasicInfoDao.insert(userBasicInfo);
                            comment.setAuthorId(userBasicInfo.getId());
                            commentDao.insert(comment);
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

    public class UploadCommentWorker extends Worker {

        private UserBasicInfoDao userBasicInfoDao;
        private AppDatabase db;
        private DtoConverter dtoConverter;
        private Retrofit retrofit;

        public UploadCommentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            dtoConverter = new DtoConverter(getApplicationContext());
            db = ApplicationContainer.getInstance().database;
            retrofit = ApplicationContainer.getInstance().retrofit;
            userBasicInfoDao = db.getUserBasicInfoDao();
        }


        @NonNull
        @Override
        public Result doWork() {
            Data data = getInputData();
            String content = data.getString("content");
            String uriPath = data.getString("image content");
            Integer postId = data.getInt("post id", -1);

            Uri image = uriPath == null ? null : Uri.parse(uriPath);
            RequestBody contentPart = HttpBodyConverter.getTextRequestBody(content);
            MultipartBody.Part mediaStreamPart = null;
            try {
                mediaStreamPart = HttpBodyConverter.getMultipartBody(image, getApplicationContext().getContentResolver(), "image_content");
                Call<CommentBody> req = retrofit.create(PostApi.class).uploadComment(postId, contentPart, mediaStreamPart);
                Response<CommentBody> res = req.execute();

                CommentBody commentBody = res.body();


                HashMap<String, Object> m = dtoConverter.convertToComment(commentBody);

                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
                        Comment comment = (Comment) m.get("comment");

                        userBasicInfoDao.insert(userBasicInfo);
                        comment.setAuthorId(userBasicInfo.getId());

                        commentDao.insert(comment);
                    }
                });

                return Result.success();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Result.failure();

        }
    }
}
