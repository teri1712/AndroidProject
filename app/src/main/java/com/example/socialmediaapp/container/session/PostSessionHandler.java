package com.example.socialmediaapp.container.session;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.PostDataSyncBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostSessionHandler extends SessionHandler {
    private Integer postId;
    private MutableLiveData<Post> dataSyncEmitter;
    private WorkManager workManager = ApplicationContainer.getInstance().workManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public PostSessionHandler(Integer postId) {
        super();
        this.postId = postId;
        dataSyncEmitter = new MutableLiveData<>();
    }

    public MutableLiveData<Post> getDataSyncEmitter() {
        return dataSyncEmitter;
    }

    public MutableLiveData<String> doLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                Data query = new Data.Builder().putString("action", "like").putInt("post id", postId).build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(LikeHandleWorker.class).setInputData(query).build();
                workManager.enqueue(req);

                ListenableFuture<WorkInfo> future = workManager.getWorkInfoById(req.getId());
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            callBack.postValue("Success");
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.postValue("Failed");
                        }
                    }
                });
            }
        });
        return callBack;
    }

    public MutableLiveData<String> doUnLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                Data query = new Data.Builder().putString("action", "unlike").putInt("post id", postId).build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(LikeHandleWorker.class).setInputData(query).build();
                workManager.enqueue(req);

                ListenableFuture<WorkInfo> future = workManager.getWorkInfoById(req.getId());
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            callBack.postValue("Success");
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.postValue("Failed");
                        }
                    }
                });
            }
        });
        return callBack;
    }

    public void requestSyncData(Integer postId) {
        post(new Runnable() {
            @Override
            public void run() {
                Data query = new Data.Builder().putInt("post id", postId).build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(DataSyncWorker.class).setInputData(query).build();
                workManager.enqueue(req);

                ListenableFuture<WorkInfo> future = workManager.getWorkInfoById(req.getId());
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            Integer countLike = out.getInt("count like", -1);
                            Integer countComment = out.getInt("count comment", -1);
                            Integer countShare = out.getInt("count share", -1);
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Post post = new Post();
                                    post.setLikeCount(countLike);
                                    post.setCommentCount(countComment);
                                    post.setShareCount(countShare);
                                    dataSyncEmitter.setValue(post);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void clean() {
        super.clean();
    }

    public class LikeHandleWorker extends Worker {
        private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;

        public LikeHandleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        private String likePost(Integer postId) throws IOException {
            Call<ResponseBody> req = retrofit.create(PostApi.class).likePost(postId);
            Response<ResponseBody> res = req.execute();
            return res.code() == 200 ? "Success" : "Failed";
        }

        private String unLikePost(Integer postId) throws IOException {
            Call<ResponseBody> req = retrofit.create(PostApi.class).unlikePost(postId);
            Response<ResponseBody> res = req.execute();
            return res.code() == 200 ? "Success" : "Failed";
        }

        @NonNull
        @Override
        public Result doWork() {
            Data query = getInputData();
            String action = query.getString("action");
            Integer postId = query.getInt("post id", -1);

            try {
                String res = action.equals("like") ? likePost(postId) : unLikePost(postId);
                return Result.success(new Data.Builder().putString("result", res).build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Result.failure();
        }
    }

    public class DataSyncWorker extends Worker {
        private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;


        public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Data query = getInputData();
            Integer postId = query.getInt("post id", -1);
            Call<PostDataSyncBody> req = retrofit.create(PostApi.class).syncPostData(postId);
            try {
                Response<PostDataSyncBody> res = req.execute();

                PostDataSyncBody body = res.body();

                return Result.success(new Data.Builder().putInt("like count", body.likeCount).putInt("comment count", body.commentCount).putInt("share count", body.shareCount).build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Result.failure();
        }
    }
}
