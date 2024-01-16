package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.dao.comment.OrderCommentDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.comment.OrderedComment;
import com.example.socialmediaapp.application.network.TaskDetails;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class CommentUploadTask extends UploadTask<HandlerAccess> {
  private OrderCommentDao orderDao;
  private CommentHandlerStore handlerStore;

  public CommentUploadTask() {
    super();
    orderDao = DecadeDatabase.getInstance().getOrderCommentDao();
    handlerStore = CommentHandlerStore.getInstance();
  }

  @Override
  public void doTask() {
    ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();
    Bundle data = getData();
    String postId = data.getString("post id");
    int cAccessId = data.getInt("comment access id");

    String content = data.getString("content");
    String uriPath = data.getString("image content");
    Uri image = uriPath == null ? null : Uri.parse(uriPath);

    Response<CommentBody> res = null;
    HandlerAccess access = null;
    try {
      RequestBody contentPart = HttpBodyConverter.getTextRequestBody(content);
      MultipartBody.Part mediaStreamPart = HttpBodyConverter.getMultipartBody(image, resolver, "image_content");
      res = HttpCallSupporter.create(CommentApi.class)
              .upload(postId, contentPart, mediaStreamPart)
              .execute();
      HttpCallSupporter.debug(res);
      CommentBody body = res.body();
      Map<String, Object> itemPack = DtoConverter.convertToComment(body);
      Comment item = (Comment) itemPack.get("comment");
      access = handlerStore.register(item, itemPack);

      OrderedComment ord = new OrderedComment();
      ord.setOrd(item.getOrd());
      ord.setCommentId(item.getId());
      ord.setCommentAccessId(cAccessId);
      ord.setAccessId(access.getId());
      orderDao.insert(ord);

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
