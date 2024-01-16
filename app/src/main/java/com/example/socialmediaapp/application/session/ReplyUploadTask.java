package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.ReplyCommentBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.comment.OrderReplyDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.OrderedComment;
import com.example.socialmediaapp.application.entity.reply.OrderedReply;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.network.TaskDetails;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class ReplyUploadTask extends UploadTask<HandlerAccess> {
  private OrderReplyDao orderDao;
  private ReplyHandlerStore handlerStore;

  public ReplyUploadTask() {
    super();
    orderDao = DecadeDatabase.getInstance().getOrderReplyDao();
    handlerStore = ReplyHandlerStore.getInstance();
  }
  @Override
  public void doTask() {
    ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();
    Bundle data = getData();
    String commentId = data.getString("comment id");
    int cAccessId = data.getInt("comment access id");

    String content = data.getString("content");
    String uriPath = data.getString("image content");
    Uri image = uriPath == null ? null : Uri.parse(uriPath);

    Response<ReplyCommentBody> res = null;
    HandlerAccess access = null;
    try {
      RequestBody contentPart = HttpBodyConverter.getTextRequestBody(content);
      MultipartBody.Part mediaStreamPart = HttpBodyConverter.getMultipartBody(image, resolver, "image_content");
      res = HttpCallSupporter.create(CommentApi.class)
              .uploadReplyComment(commentId, contentPart, mediaStreamPart)
              .execute();
      HttpCallSupporter.debug(res);
      ReplyCommentBody body = res.body();
      Map<String, Object> itemPack = DtoConverter.convertToReply(body);
      ReplyComment item = (ReplyComment) itemPack.get("comment");
      access = handlerStore.register(item, itemPack);

      OrderedReply ord = new OrderedReply();
      ord.setOrd(item.getOrd());
      ord.setReplyCommentId(item.getId());
      ord.setReplyCommentAccessId(cAccessId);
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
            /* TODO : You can find the Comment session where it contains the comment access handler
                 then call the onItemUpload
                 handlerStore.getRegistry(commentId).sessionHandler
                 */
          }
        });
      }
    }
  }
}
