package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.apis.CommentApi;
import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.CommentDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
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
   private Integer postId;
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
   private AppDatabase db = ApplicationContainer.getInstance().database;
   private CommentDao commentDao;
   private UserBasicInfoDao userBasicInfoDao;
   private SequenceDao sequenceDao;

   public CommentAccessHelper(Integer postId) {
      super();
      this.postId = postId;
      commentDao = db.getCommentDao();
      userBasicInfoDao = db.getUserBasicInfoDao();
      sequenceDao = db.getSequenceDao();
   }

   @Override
   public List<com.example.socialmediaapp.viewmodel.models.post.Comment> loadFromLocalStorage(HashMap<String, Object> query) {
      com.example.socialmediaapp.viewmodel.models.post.Comment lastItem = (com.example.socialmediaapp.viewmodel.models.post.Comment) query.get("last item");
      int length = (int) query.get("length");
      List<Comment> comments = commentDao.loadComments(lastItem.getId(), session.getId(), length);
      List<com.example.socialmediaapp.viewmodel.models.post.Comment> res = new ArrayList<>();
      for (Comment c : comments) {
         com.example.socialmediaapp.viewmodel.models.post.Comment comment = new com.example.socialmediaapp.viewmodel.models.post.Comment();
         comment.setId(c.getId());
         comment.setContent(c.getContent());
         comment.setLiked(c.isLiked());
         comment.setTime(c.getTime());
         comment.setCountLike(c.getLikeCount());
         comment.setCountComment(c.getCommentCount());
         if (c.getImageUri() != null) {
            comment.setImage(BitmapFactory.decodeFile(c.getImageUri()));
         }
         UserBasicInfo u = userBasicInfoDao.findUserBasicInfo(c.getUserInfoId());
         com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
         userBasicInfo.setFullname(u.getFullname());
         userBasicInfo.setAlias(u.getAlias());
         if (u.getAvatarUri() != null) {
            userBasicInfo.setAvatar(BitmapFactory.decodeFile(u.getAvatarUri()));
         }
         comment.setAuthor(userBasicInfo);
         res.add(comment);
      }
      return res;
   }

   @Override
   public Bundle loadFromServer() throws IOException {
      Bundle result = new Bundle();

      int lastId = commentDao.findLastComment(session.getId()).getId();
      Call<List<CommentBody>> req = retrofit.create(CommentApi.class).loadComment(postId, lastId);
      Response<List<CommentBody>> res = req.execute();
      List<CommentBody> commentBodies = res.body();
      final List<HashMap<String, Object>> comments = new ArrayList<>();
      for (CommentBody c : commentBodies) {
         comments.add(dtoConverter.convertToComment(c, session.getId()));
      }
      db.runInTransaction(() -> {
         Integer ordSeq = lastId;
         for (HashMap<String, Object> m : comments) {
            UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
            int userId = (int) userBasicInfoDao.insert(userBasicInfo);

            Comment comment = (Comment) m.get("comment");
            comment.setOrd(++ordSeq);
            comment.setUserInfoId(userId);
            comment.setSessionId(session.getId());
            commentDao.insert(comment);

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
      MultipartBody.Part mediaStreamPart = HttpBodyConverter.getMultipartBody(image, ApplicationContainer.getInstance().getContentResolver(), "image_content");
      Call<CommentBody> req = retrofit.create(CommentApi.class).upload(postId, contentPart, mediaStreamPart);
      Response<CommentBody> res = req.execute();

      CommentBody commentBody = res.body();
      HashMap<String, Object> m = dtoConverter.convertToComment(commentBody, session.getId());

      db.runInTransaction(() -> {
         UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
         int userId = (int) userBasicInfoDao.insert(userBasicInfo);

         Comment comment = (Comment) m.get("comment");
         comment.setOrd(sequenceDao.getHeadValue());
         comment.setUserInfoId(userId);
         commentDao.insert(comment);
      });
      return dtoConverter.convertToModelComment(commentBody);
   }

   @Override
   public void cleanAll() {
      List<Comment> comments = commentDao.findAllBySession(session.getId());
      for (Comment c : comments) {
         userBasicInfoDao.deleteById(c.getUserInfoId());
         commentDao.deleteComment(c);
      }

      for (String fn : dtoConverter.getCachedFiles()) {
         File file = new File(fn);
         file.delete();
      }
   }
}
