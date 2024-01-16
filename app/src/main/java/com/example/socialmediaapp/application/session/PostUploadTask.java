package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.post.OrderPostDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.OrderedPost;
import com.example.socialmediaapp.application.entity.post.Post;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class PostUploadTask extends UploadTask<HandlerAccess> {
  private OrderPostDao orderDao;
  private SequenceDao seqDao;
  private PostHandlerStore handlerStore;

  public PostUploadTask() {
    super();
    seqDao = DecadeDatabase.getInstance().getSequenceDao();
    orderDao = DecadeDatabase.getInstance().getOrderPostDao();
    handlerStore = PostHandlerStore.getInstance();
  }
  @Override
  public void doTask() {
    Bundle data = getData();
    ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();
    int pAccessId = data.getInt("post access id");

    String content = data.getString("post content");
    String uriPath = data.getString("media content");
    Uri mediaContent = uriPath == null ? null : Uri.parse(uriPath);
    String type = mediaContent == null ? "text" : resolver.getType(mediaContent);

    HandlerAccess access = null;
    try {
      RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
      RequestBody mediaTypeBody = HttpBodyConverter.getTextRequestBody(type);
      MultipartBody.Part mediaBody = HttpBodyConverter.getMultipartBody(
              mediaContent,
              resolver,
              "media_data");
      Response<PostBody> res = HttpCallSupporter.create(PostApi.class)
              .upload(contentBody, mediaTypeBody, mediaBody)
              .execute();
      HttpCallSupporter.debug(res);

      PostBody body = res.body();
      Map<String, Object> itemPack = DtoConverter.convertToPost(body);
      Post item = (Post) itemPack.get("post");

      access = handlerStore.register(item, itemPack);
      OrderedPost orderedPost = new OrderedPost();
      orderedPost.setPostId(item.getId());
      orderedPost.setId(seqDao.getHeadValue());
      orderedPost.setPostAccessId(pAccessId);
      orderedPost.setAccessId(access.getId());
      orderDao.insert(orderedPost);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      String status = access == null ? "Failed" : "Success";
      if (uploadHelper != null) {
        uploadHelper.onItemUploaded(status, access);
      } else {
        /* NOTE : this is the case where the task is restart while it hasn't completed */
        handlerStore.post(new Runnable() {
          @Override
          public void run() {
            /* TODO : You can find the Post session where it contains the comment access handler
                 then call the onItemUpload
                 handlerStore.getRegistry(postId).sessionHandler
                 */
          }
        });
      }
    }
  }
}
