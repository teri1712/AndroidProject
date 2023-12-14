package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.apis.CommentApi;
import com.example.socialmediaapp.apis.entities.ReplyCommentBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.CommentDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.ReplyComment;
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

public class ReplyCommentAccessHelper extends DataAccessHelper<com.example.socialmediaapp.viewmodel.models.post.ReplyComment> {
   private CommentDao commentDao;
   private UserBasicInfoDao userBasicInfoDao;
   private SequenceDao sequenceDao;
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
   private Integer commentId;
   private AppDatabase db = ApplicationContainer.getInstance().database;

   public ReplyCommentAccessHelper(Integer commentId) {
      super();
      this.commentId = commentId;
      commentDao = db.getCommentDao();
      userBasicInfoDao = db.getUserBasicInfoDao();
      sequenceDao = db.getSequenceDao();
   }


   @Override
   public List<com.example.socialmediaapp.viewmodel.models.post.ReplyComment> loadFromLocalStorage(HashMap<String, Object> query) {
      com.example.socialmediaapp.viewmodel.models.post.ReplyComment lastItem = (com.example.socialmediaapp.viewmodel.models.post.ReplyComment) query.get("last item");
      int length = (int) query.get("length");
      List<ReplyComment> comments = commentDao.loadReplyComments(lastItem.getId(), session.getId(), length);
      List<com.example.socialmediaapp.viewmodel.models.post.ReplyComment> res = new ArrayList<>();
      for (ReplyComment c : comments) {
         com.example.socialmediaapp.viewmodel.models.post.ReplyComment comment = new com.example.socialmediaapp.viewmodel.models.post.ReplyComment();
         comment.setId(c.getId());
         comment.setContent(c.getContent());
         comment.setLiked(c.isLiked());
         comment.setTime(c.getTime());
         comment.setCountLike(c.getLikeCount());
         if (c.getImageUri() != null) {
            comment.setImage(BitmapFactory.decodeFile(c.getImageUri()));
         }
         UserBasicInfo u = userBasicInfoDao.findUserBasicInfo(c.getAutoId());
         com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
         userBasicInfo.setFullname(u.getFullname());
         userBasicInfo.setAlias(u.getAlias());
         if (u.getAvatarUri() != null) {
            userBasicInfo.setAvatar(BitmapFactory.decodeFile(u.getAvatarUri()));
         }
         comment.setSender(userBasicInfo);
         res.add(comment);
      }
      return res;
   }

   @Override
   public Bundle loadFromServer() throws IOException {
      int lastId = commentDao.findLastReplyComment(session.getId()).getId();
      Call<List<ReplyCommentBody>> req = retrofit.create(CommentApi.class).loadReplyComment(commentId, lastId);
      Response<List<ReplyCommentBody>> res = req.execute();
      List<ReplyCommentBody> commentBodies = res.body();
      final List<HashMap<String, Object>> comments = new ArrayList<>();
      for (ReplyCommentBody c : commentBodies) {
         comments.add(dtoConverter.convertToReplyComment(c, session.getId()));
      }
      db.runInTransaction(() -> {
         for (HashMap<String, Object> m : comments) {
            UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
            int userId = (int) userBasicInfoDao.insert(userBasicInfo);

            ReplyComment comment = (ReplyComment) m.get("comment");
            comment.setOrd(sequenceDao.getTailValue());
            comment.setUserInfoId(userId);
            comment.setSessionId(session.getId());
            commentDao.insertReplyComment(comment);

         }
      });
      Bundle result = new Bundle();
      result.putInt("count loaded", commentBodies.size());
      return result;
   }

   @Override
   public com.example.socialmediaapp.viewmodel.models.post.ReplyComment uploadToServer(Bundle query) throws IOException, FileNotFoundException {
      String content = query.getString("content");
      String uriPath = query.getString("image content");

      Uri image = uriPath == null ? null : Uri.parse(uriPath);
      RequestBody contentPart = HttpBodyConverter.getTextRequestBody(content);
      MultipartBody.Part mediaStreamPart = HttpBodyConverter.getMultipartBody(image, ApplicationContainer.getInstance().getContentResolver(), "image_content");
      Call<ReplyCommentBody> req = retrofit.create(CommentApi.class).uploadReplyComment(commentId, contentPart, mediaStreamPart);
      Response<ReplyCommentBody> res = req.execute();


      ReplyCommentBody commentBody = res.body();
      HashMap<String, Object> m = dtoConverter.convertToReplyComment(commentBody, session.getId());
      db.runInTransaction(() -> {
         UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
         int userId = (int) userBasicInfoDao.insert(userBasicInfo);

         ReplyComment comment = (ReplyComment) m.get("comment");
         comment.setOrd(sequenceDao.getHeadValue());
         comment.setUserInfoId(userId);
         commentDao.insertReplyComment(comment);
      });
      return dtoConverter.convertToModelReplyComment(commentBody);
   }

   @Override
   public void cleanAll() {
      List<ReplyComment> comments = commentDao.findAllReplyBySession(session.getId());
      for (ReplyComment comment : comments) {
         userBasicInfoDao.deleteById(comment.getUserInfoId());
         commentDao.deleteReplyComment(comment);
      }
      for (String fn : dtoConverter.getCachedFiles()) {
         File file = new File(fn);
         file.delete();
      }
   }
}
