package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.CommentDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.Comment;
import com.example.socialmediaapp.application.entity.UserBasicInfo;

import java.io.File;
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
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
    private Integer postId;
    private int ordSeq;
    private AppDatabase db = ApplicationContainer.getInstance().database;

    public CommentAccessHelper(Integer postId) {
        super();
        this.postId = postId;
        ordSeq = 1;
        commentDao = db.getCommentDao();
        userBasicInfoDao = db.getUserBasicInfoDao();
    }


    @Override
    public List<com.example.socialmediaapp.viewmodel.models.post.Comment> tryToFetchFromLocalStorage(Bundle query) {
        int countLoaded = query.getInt("count loaded");
        List<Comment> comments = commentDao.getComments(countLoaded, session.getId());
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
            res.add(comment);
        }
        return res;
    }

    @Override
    public Bundle fetchFromServer(Bundle query) throws IOException {
        Bundle result = new Bundle();
        Call<List<CommentBody>> req = retrofit.create(PostApi.class).fetchComments(postId);
        Response<List<CommentBody>> res = req.execute();
        List<CommentBody> commentBodies = res.body();
        final List<HashMap<String, Object>> comments = new ArrayList<>();
        for (CommentBody c : commentBodies) {
            comments.add(dtoConverter.convertToComment(c, session.getId()));
        }
        db.runInTransaction(new Runnable() {
            @Override
            public void run() {
                ordSeq++;
                for (HashMap<String, Object> m : comments) {
                    Comment comment = (Comment) m.get("comment");
                    comment.setOrd(ordSeq);
                    UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
                    comment.setAuthorId((int) userBasicInfoDao.insert(userBasicInfo));
                    commentDao.insert(comment);
                }
            }
        });
        result.putInt("count loaded", commentBodies.size());
        return result;
    }

    @Override
    public com.example.socialmediaapp.viewmodel.models.post.Comment uploadToServer(Bundle query) throws IOException, FileNotFoundException {
        String content = query.getString("content");
        String uriPath = query.getString("image content");

        Uri image = uriPath == null ? null : Uri.parse(uriPath);
        RequestBody contentPart = HttpBodyConverter.getTextRequestBody(content);
        MultipartBody.Part mediaStreamPart = null;
        mediaStreamPart = HttpBodyConverter.getMultipartBody(image, ApplicationContainer.getInstance().getContentResolver(), "image_content");
        Call<CommentBody> req = retrofit.create(PostApi.class).uploadComment(postId, contentPart, mediaStreamPart);
        Response<CommentBody> res = req.execute();

        CommentBody commentBody = res.body();
        return dtoConverter.convertToModelComment(commentBody);
    }

    @Override
    public void clean() {
        commentDao.deleteAll(session.getId());
        userBasicInfoDao.deleteAll(session.getId());
        ArrayList<String> cached = dtoConverter.getCachedFiles();
        for (String fn : cached) {
            File file = new File(fn);
            file.delete();
        }
    }

}
