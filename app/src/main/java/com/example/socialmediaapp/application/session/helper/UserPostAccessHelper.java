package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.PostDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.ImagePost;
import com.example.socialmediaapp.application.entity.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.post.MediaPost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserPostAccessHelper extends DataAccessHelper<com.example.socialmediaapp.viewmodel.models.post.base.Post> {
    private String userAlias;
    private PostDao postDao;
    private UserBasicInfoDao userBasicInfoDao;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
    private Integer ordSeq;
    private AppDatabase db = ApplicationContainer.getInstance().database;
    public UserPostAccessHelper(String userAlias) {
        this.userAlias = userAlias;
        ordSeq = 1;
        postDao = db.getPostDao();
        userBasicInfoDao = db.getUserBasicInfoDao();
    }


    @Override
    public List<Post> tryToFetchFromLocalStorage(Bundle query) {
        int countLoaded = query.getInt("count loaded", 0);
        List<com.example.socialmediaapp.application.entity.Post> posts = postDao.getPosts(countLoaded, session.getId());
        List<com.example.socialmediaapp.viewmodel.models.post.base.Post> res = new ArrayList<>();
        for (com.example.socialmediaapp.application.entity.Post p : posts) {
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
        Call<List<PostBody>> req = retrofit.create(PostApi.class).fetchPostFromUser(userAlias);
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
                    com.example.socialmediaapp.application.entity.Post post = (com.example.socialmediaapp.application.entity.Post) m.get("post");
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
    public Post uploadToServer(Bundle query) throws IOException, FileNotFoundException {
        return null;
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
