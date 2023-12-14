package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.PostDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
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
   private SequenceDao sequenceDao;
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private DtoConverter dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
   private AppDatabase db = ApplicationContainer.getInstance().database;

   public UserPostAccessHelper(String userAlias) {
      this.userAlias = userAlias;
      postDao = db.getPostDao();
      userBasicInfoDao = db.getUserBasicInfoDao();
      sequenceDao = db.getSequenceDao();
   }

   @Override
   public List<Post> loadFromLocalStorage(HashMap<String, Object> query) {
      Post item = (Post) query.get("last item");
      int length = (int) query.get("length");
      List<com.example.socialmediaapp.application.entity.Post> posts = postDao.loadPostsByOrder(item.getOrder(), session.getId(), length);

      List<com.example.socialmediaapp.viewmodel.models.post.base.Post> res = new ArrayList<>();
      for (com.example.socialmediaapp.application.entity.Post p : posts) {
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
      int lastId = postDao.findLastPostOfUser(session.getId()).getId();
      Call<List<PostBody>> req = retrofit.create(PostApi.class).loadPostsOfUser(userAlias, lastId);
      Response<List<PostBody>> res = req.execute();
      List<PostBody> postBodies = res.body();
      List<HashMap<String, Object>> posts = new ArrayList<>();
      for (PostBody postBody : postBodies) {
         HashMap<String, Object> m = dtoConverter.convertToPost(postBody, session.getId());
         posts.add(m);
      }
      db.runInTransaction(() -> {
         for (HashMap<String, Object> m : posts) {
            com.example.socialmediaapp.application.entity.Post post = (com.example.socialmediaapp.application.entity.Post) m.get("post");
            ImagePost imagePost = (ImagePost) m.get("image post");
            com.example.socialmediaapp.application.entity.MediaPost mediaPost = (com.example.socialmediaapp.application.entity.MediaPost) m.get("media post");
            UserBasicInfo userBasicInfo = (UserBasicInfo) m.get("user basic info");
            int userId = (int) userBasicInfoDao.insert(userBasicInfo);

            post.setSessionId(session.getId());
            post.setOrd(sequenceDao.getTailValue());
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
   public void cleanAll() {
      List<com.example.socialmediaapp.application.entity.Post> posts = postDao.findAllBySession(session.getId());
      for (com.example.socialmediaapp.application.entity.Post p : posts) {
         userBasicInfoDao.deleteById(p.getUserInfoId());
         postDao.deletePost(p);
      }
      for (String fn : dtoConverter.getCachedFiles()) {
         File file = new File(fn);
         file.delete();
      }
   }
}
