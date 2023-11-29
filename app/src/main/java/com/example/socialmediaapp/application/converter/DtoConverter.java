package com.example.socialmediaapp.application.converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.socialmediaapp.apis.MediaApi;
import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.apis.entities.UserSessionBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.entity.Comment;
import com.example.socialmediaapp.application.entity.ImagePost;
import com.example.socialmediaapp.application.entity.Post;
import com.example.socialmediaapp.application.entity.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.post.MediaPost;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.models.user.profile.NotMeProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.SelfProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DtoConverter {
    private Context context;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    final private HttpExtraResolver httpExtraResolver = new HttpExtraResolver();
    private ArrayList<String> cachedFiles;

    public DtoConverter(Context context) {
        this.context = context;
        cachedFiles = new ArrayList<>();
    }

    public ArrayList<String> getCachedFiles() {
        return cachedFiles;
    }

    public HashMap<String, Object> convertToPost(PostBody postBody, Integer sessionId) throws IOException {
        HashMap<String, Object> res = new HashMap<>();
        Post p = new Post();
        p.setId(postBody.getId());
        p.setCommentCount(postBody.getCommentCount());
        p.setLikeCount(postBody.getLikeCount());
        p.setShareCount(postBody.getShareCount());
        p.setLiked(postBody.isLiked());
        p.setStatus(postBody.getStatus());
        p.setTime(postBody.getTime());
        p.setType(postBody.getType());
        p.setSessionId(sessionId);
        res.put("post", p);

        Integer mediaId = postBody.getMediaId();

        if (Objects.equals("image", postBody.getType())) {
            ImagePost imagePost = new ImagePost();
            imagePost.setPostId(p.getId());
            imagePost.setImageUri(httpExtraResolver.cacheImageFile(sessionId, mediaId));
            res.put("image post", imagePost);
            imagePost.setSessionId(sessionId);
        } else {
            com.example.socialmediaapp.application.entity.MediaPost mediaPost = new com.example.socialmediaapp.application.entity.MediaPost();
            mediaPost.setPostId(p.getId());
            mediaPost.setMediaId(mediaId);
            res.put("media post", mediaPost);
            mediaPost.setSessionId(sessionId);
        }

        res.put("user basic info", convertToUserBasicInfo(postBody.getAuthor(), sessionId));

        return res;
    }

    public com.example.socialmediaapp.viewmodel.models.post.base.Post convertToModelPost(PostBody postBody) throws IOException {
        com.example.socialmediaapp.viewmodel.models.post.base.Post post = null;
        if (postBody.getType().equals("image")) {
            com.example.socialmediaapp.viewmodel.models.post.ImagePost imagePost = new com.example.socialmediaapp.viewmodel.models.post.ImagePost();
            imagePost.setImage(httpExtraResolver.getImageBitmap(postBody.getMediaId()));
            post = imagePost;
        } else if (post.getType().equals("media")) {
            MediaPost mediaPost = new MediaPost();
            mediaPost.setMediaId(postBody.getMediaId());
            post = mediaPost;
        }
        post.setId(postBody.getId());
        post.setAuthor(convertToModelUserBasicInfo(postBody.getAuthor()));
        post.setCommentCount(postBody.getCommentCount());
        post.setShareCount(postBody.getShareCount());
        post.setLikeCount(postBody.getLikeCount());
        post.setStatus(postBody.getStatus());
        post.setTime(postBody.getTime());
        post.setType(postBody.getType());
        return post;
    }

    public UserBasicInfo convertToUserBasicInfo(UserBasicInfoBody userBasicInfoBody, Integer sessionId) throws IOException {
        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setFullname(userBasicInfoBody.getFullname());
        userBasicInfo.setAlias(userBasicInfoBody.getAlias());
        userBasicInfo.setSessionId(sessionId);
        userBasicInfo.setAvatarUri(httpExtraResolver.cacheImageFile(sessionId, userBasicInfoBody.getAvatarId()));
        return userBasicInfo;
    }


    public com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo convertToModelUserBasicInfo(UserBasicInfoBody userBasicInfoBody) throws IOException {
        com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo userBasicInfo = new com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo();
        userBasicInfo.setFullname(userBasicInfoBody.getFullname());
        userBasicInfo.setAlias(userBasicInfoBody.getAlias());
        userBasicInfo.setAvatar(httpExtraResolver.getImageBitmap(userBasicInfoBody.getAvatarId()));
        return userBasicInfo;
    }

    public HashMap<String, Object> convertToComment(CommentBody commentBody, Integer sessionId) throws IOException {
        HashMap<String, Object> m = new HashMap<>();
        Comment comment = new Comment();
        comment.setContent(commentBody.getContent());
        comment.setTime(commentBody.getTime());
        comment.setLiked(commentBody.isLiked());
        comment.setLikeCount(commentBody.getCountLike());
        comment.setSessionId(sessionId);
        m.put("comment", comment);
        UserBasicInfo userBasicInfo = convertToUserBasicInfo(commentBody.getAuthor(), sessionId);
        m.put("user basic info", userBasicInfo);
        return m;
    }

    public com.example.socialmediaapp.viewmodel.models.post.Comment convertToModelComment(CommentBody commentBody) throws IOException {
        com.example.socialmediaapp.viewmodel.models.post.Comment comment = new com.example.socialmediaapp.viewmodel.models.post.Comment();
        comment.setAuthor(convertToModelUserBasicInfo(commentBody.getAuthor()));
        comment.setId(commentBody.getId());
        comment.setContent(commentBody.getContent());
        comment.setTime(commentBody.getTime());
        comment.setCountLike(commentBody.getCountLike());
        comment.setCountComment(commentBody.getCountComment());
        comment.setImage(httpExtraResolver.getImageBitmap(commentBody.getMediaId()));
        return comment;
    }

    public UserSession convertToUserSession(UserSessionBody userSessionBody) throws IOException {
        UserInformation userInformation = new UserInformation();
        userInformation.setFullname(userSessionBody.getUserInfo().getFullname());
        userInformation.setAlias(userSessionBody.getUserInfo().getAlias());
        userInformation.setGender(userSessionBody.getUserInfo().getGender());
        userInformation.setBirthday(userSessionBody.getUserInfo().getBirthday());
        UserSession userSession = new UserSession();
        userSession.setUserInfo(userInformation);
        userSession.setAvatar(httpExtraResolver.getImageBitmap(userSessionBody.getAvatarId()));
        userSession.setBackground(httpExtraResolver.getImageBitmap(userSessionBody.getBackgroundId()));
        return userSession;
    }

    public UserProfile convertToUserProfile(UserProfileBody userProfileBody) {
        UserProfile userProfile = (userProfileBody.getType().equals("self")) ? new SelfProfile() : new NotMeProfile();
        userProfile.setFullname(userProfile.getFullname());
        userProfile.setAlias(userProfile.getAlias());
        userProfile.setGender(userProfile.getGender());
        userProfile.setBirthday(userProfile.getBirthday());

        return userProfile;

    }

    private class HttpExtraResolver {

        private String cacheImageFile(Integer sessionId, Integer mediaId) throws IOException {
            if (mediaId == null) return null;
            File cacheDir = context.getCacheDir();
            File cache = new File(cacheDir, Integer.toString(sessionId) + "#" + Integer.toString(mediaId));
            cache.createNewFile();
            FileOutputStream fos = new FileOutputStream(cache);
            Response<ResponseBody> img = retrofit.create(MediaApi.class).getImage(mediaId).execute();
            InputStream is = img.body().byteStream();
            byte[] buffer = new byte[2048];
            int cur = 0;
            long total = img.body().contentLength();
            while (cur != total) {
                int cnt = is.read(buffer, 0, Math.min((int) total - cur, 2048));
                cur += cnt;
                fos.write(buffer, 0, cnt);
            }
            is.close();
            fos.close();
            cachedFiles.add(cache.getAbsolutePath());
            return cache.getAbsolutePath();
        }

        private Bitmap getImageBitmap(Integer mediaId) throws IOException {
            if (mediaId == null) return null;
            Response<ResponseBody> res = retrofit.create(MediaApi.class).getImage(mediaId).execute();
            byte[] img = res.body().bytes();
            return BitmapFactory.decodeByteArray(img, 0, img.length);
        }
    }
}
