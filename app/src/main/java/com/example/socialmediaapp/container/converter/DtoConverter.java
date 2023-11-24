package com.example.socialmediaapp.container.converter;

import android.content.Context;

import com.example.socialmediaapp.apis.MediaApi;
import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.entity.Comment;
import com.example.socialmediaapp.container.entity.ImagePost;
import com.example.socialmediaapp.container.entity.Post;
import com.example.socialmediaapp.container.entity.UserBasicInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DtoConverter {
    private Context context;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    final private HttpExtraResolver httpExtraResolver = new HttpExtraResolver();

    public DtoConverter(Context context) {
        this.context = context;
    }

    public HashMap<String, Object> convertToPost(PostBody postBody) throws IOException {
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
        res.put("post", p);

        Integer mediaId = postBody.getMediaId();

        if (Objects.equals("image", postBody.getType())) {
            ImagePost imagePost = new ImagePost();
            imagePost.setPostId(p.getId());
            imagePost.setImageUri(httpExtraResolver.getImageFile(mediaId));
            res.put("image post", imagePost);
        } else {
            com.example.socialmediaapp.container.entity.MediaPost mediaPost = new com.example.socialmediaapp.container.entity.MediaPost();
            mediaPost.setPostId(p.getId());
            mediaPost.setMediaId(mediaId);
            res.put("media post", mediaPost);
        }

        res.put("user basic info", convertToUserBasicInfo(postBody.getAuthor()));

        return res;
    }

    public UserBasicInfo convertToUserBasicInfo(UserBasicInfoBody userBasicInfoBody) throws IOException {
        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setFullname(userBasicInfoBody.getFullname());
        userBasicInfo.setAlias(userBasicInfoBody.getAlias());
        userBasicInfo.setAvatarUri(httpExtraResolver.getImageFile(userBasicInfoBody.getAvatarId()));
        return userBasicInfo;
    }

    public HashMap<String, Object> convertToComment(CommentBody commentBody) throws IOException {
        HashMap<String, Object> m = new HashMap<>();
        Comment comment = new Comment();
        comment.setContent(commentBody.getContent());
        comment.setTime(commentBody.getTime());
        comment.setLiked(commentBody.isLiked());
        comment.setLikeCount(commentBody.getCountLike());
        m.put("comment", comment);
        UserBasicInfo userBasicInfo = convertToUserBasicInfo(commentBody.getAuthor());
        m.put("user basic info", userBasicInfo);
        return m;
    }

    private class HttpExtraResolver {

        //return the location of cached file
        private String getImageFile(Integer mediaId) throws IOException {
            File cacheDir = context.getCacheDir();
            File cache = new File(cacheDir, Integer.toString(mediaId));
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
            return cache.getAbsolutePath();
        }
    }
}
