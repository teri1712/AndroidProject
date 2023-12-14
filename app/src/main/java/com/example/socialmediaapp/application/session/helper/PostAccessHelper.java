package com.example.socialmediaapp.application.session.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.PostDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.ImagePost;
import com.example.socialmediaapp.application.entity.Post;
import com.example.socialmediaapp.application.entity.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.post.MediaPost;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostAccessHelper extends DataAccessHelper<com.example.socialmediaapp.viewmodel.models.post.base.Post> {
   private PostDao postDao;
   private SequenceDao sequenceDao;
   private UserBasicInfoDao userBasicInfoDao;
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
   private AppDatabase db = ApplicationContainer.getInstance().database;

   public PostAccessHelper() {
      super();
      postDao = db.getPostDao();
      sequenceDao = db.getSequenceDao();
      userBasicInfoDao = db.getUserBasicInfoDao();
   }


   @Override
   public List<com.example.socialmediaapp.viewmodel.models.post.base.Post> loadFromLocalStorage(HashMap<String, Object> query) {
      com.example.socialmediaapp.viewmodel.models.post.base.Post lastItem = (com.example.socialmediaapp.viewmodel.models.post.base.Post) query.get("last item");
      int last = lastItem.getOrder();
      int length = (int) query.get("length");

      List<Post> posts = postDao.loadPostsByOrder(last, session.getId(), length);
      List<com.example.socialmediaapp.viewmodel.models.post.base.Post> res = new ArrayList<>();
      for (Post p : posts) {
         com.example.socialmediaapp.viewmodel.models.post.base.Post post = null;
         if (Objects.equals("image", p.getType())) {
            com.example.socialmediaapp.viewmodel.models.post.ImagePost imagePost = new com.example.socialmediaapp.viewmodel.models.post.ImagePost();
            post = imagePost;
            ImagePost imagePostEnity = postDao.findImagePostByPostId(p.getAutoId());
            imagePost.setImage(BitmapFactory.decodeFile(imagePostEnity.getImageUri()));

         } else if (Objects.equals("media", p.getType())) {
            MediaPost mediaPost = new com.example.socialmediaapp.viewmodel.models.post.MediaPost();
            com.example.socialmediaapp.application.entity.MediaPost mediaPostEntity = postDao.findMediaPostByPostId(p.getAutoId());
            mediaPost.setMediaId(mediaPostEntity.getMediaId());
            post = mediaPost;
         } else {
            post = new com.example.socialmediaapp.viewmodel.models.post.base.Post();
         }
         UserBasicInfo u = userBasicInfoDao.findUserBasicInfo(p.getUserInfoId());


         com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
         userBasicInfo.setFullname(u.getFullname());
         userBasicInfo.setAlias(u.getAlias());
         if (u.getAvatarUri() != null) {
            userBasicInfo.setAvatar(BitmapFactory.decodeFile(u.getAvatarUri()));
         }

         post.setAuthor(userBasicInfo);
         post.setId(p.getId());
         post.setCommentCount(p.getCommentCount());
         post.setLikeCount(p.getLikeCount());
         post.setShareCount(p.getShareCount());
         post.setTime(p.getTime());
         post.setType(p.getType());
         post.setStatus(p.getStatus());
         post.setLiked(p.isLiked());
         post.setOrder(p.getOrd());

         res.add(post);
      }
      return res;
   }

   @Override
   public Bundle loadFromServer() throws IOException {
      Bundle result = new Bundle();
      Call<List<PostBody>> req = retrofit.create(PostApi.class).load();
      Response<List<PostBody>> res = req.execute();
      List<PostBody> postBodies = res.body();
      List<HashMap<String, Object>> posts = new ArrayList<>();
      for (PostBody postBody : postBodies) {
         HashMap<String, Object> m = dtoConverter.convertToPost(postBody, session.getId());
         posts.add(m);
      }
      db.runInTransaction(() -> {
         for (HashMap<String, Object> m : posts) {
            Post post = (Post) m.get("post");
            ImagePost imagePost = (ImagePost) m.get("image post");
            com.example.socialmediaapp.application.entity.MediaPost mediaPost = (com.example.socialmediaapp.application.entity.MediaPost) m.get("media post");
            UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
            int userId = (int) userBasicInfoDao.insert(userBasicInfo);

            post.setSessionId(session.getId());
            post.setUserInfoId(userId);
            post.setOrd(sequenceDao.getTailValue());
            int id = (int) postDao.insert(post);

            if (imagePost != null) {
               imagePost.setPostId(id);
               postDao.insertImagePost(imagePost);
            }
            if (mediaPost != null) {
               mediaPost.setPostId(id);
               postDao.insertMediaPost(mediaPost);
            }
         }
      });
      result.putInt("count loaded", postBodies.size());
      return result;
   }

   @Override
   public com.example.socialmediaapp.viewmodel.models.post.base.Post uploadToServer(Bundle query) throws IOException {
      ContentResolver resolver = ApplicationContainer.getInstance().getContentResolver();

      String actionType = query.getString("type");
      String content = query.getString("post content");
      String uriPath = query.getString("media content");
      Uri mediaContent = uriPath == null ? null : Uri.parse(uriPath);
      String type = mediaContent == null ? "text" : resolver.getType(mediaContent);

      RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
      RequestBody mediaTypeBody = HttpBodyConverter.getTextRequestBody(type);
      MultipartBody.Part mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, resolver, "media_data");

      Call<PostBody> req = null;
      if (actionType.equals("avatar")) {
         req = retrofit.create(UserApi.class).changeAvatar(contentBody, mediaBody);
      } else if (actionType.equals("background")) {
         req = retrofit.create(UserApi.class).changeBackground(contentBody, mediaBody);
      } else {
         req = retrofit.create(PostApi.class).upload(contentBody, mediaTypeBody, mediaBody);
      }


      PostBody postBody = req.execute().body();
      HashMap<String, Object> m = dtoConverter.convertToPost(postBody, session.getId());
      final int ord = sequenceDao.getHeadValue();
      db.runInTransaction(() -> {

         Post post = (Post) m.get("post");
         ImagePost imagePost = (ImagePost) m.get("image post");
         com.example.socialmediaapp.application.entity.MediaPost mediaPost = (com.example.socialmediaapp.application.entity.MediaPost) m.get("media post");
         UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
         int userId = (int) userBasicInfoDao.insert(userBasicInfo);

         post.setOrd(ord);
         post.setSessionId(session.getId());
         post.setUserInfoId(userId);
         int postId = (int) postDao.insert(post);

         if (imagePost != null) {
            imagePost.setPostId(postId);
            postDao.insertImagePost(imagePost);
         }
         if (mediaPost != null) {
            mediaPost.setPostId(postId);
            postDao.insertMediaPost(mediaPost);
         }
      });
      com.example.socialmediaapp.viewmodel.models.post.base.Post res = dtoConverter.convertToModelPost(postBody);
      res.setOrder(ord);
      return res;
   }

   @Override
   public void cleanAll() {
      List<Post> posts = postDao.findAllBySession(session.getId());
      for (Post p : posts) {
         userBasicInfoDao.deleteById(p.getUserInfoId());
         postDao.deletePost(p);
      }
      for (String fn : dtoConverter.getCachedFiles()) {
         File file = new File(fn);
         file.delete();
      }
      Context context = ApplicationContainer.getInstance();
      new File(context.getCacheDir(), "SessionCache#" + Integer.toString(session.getId())).delete();
   }

   @Override
   public void popRead(com.example.socialmediaapp.viewmodel.models.post.base.Post lastItem) {
      List<String> cached = new ArrayList<>();
      db.runInTransaction(() -> {
         Integer sessionId = session.getId();
         Post post = postDao.findPostById(lastItem.getId(), sessionId);
         int ord = post.getOrd();
         List<Post> posts = postDao.findAllPostByBound(ord, sessionId);
         for (Post p : posts) {
            UserBasicInfo userBasicInfo = userBasicInfoDao.findUserBasicInfo(p.getUserInfoId());
            cached.add(userBasicInfo.getAvatarUri());

            userBasicInfoDao.delete(userBasicInfo);
            postDao.deletePost(p);
         }
      });
      for (String f : cached) {
         dtoConverter.getCachedFiles().remove(f);
         new File(f).delete();
      }
   }
}
