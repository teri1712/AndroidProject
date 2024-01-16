package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.comment.OrderCommentDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.comment.CommentAccess;
import com.example.socialmediaapp.application.entity.comment.OrderedComment;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class CommentAccessHelper
        extends DataAccessHelper<HandlerAccess>
        implements DataUpdateHelper<HandlerAccess> {
  private final OrderCommentDao dao;
  private final CommentAccess commentAccess;
  private final CommentHandlerStore handlerStore;
  private final int cAccessId;
  private final String postId;

  public CommentAccessHelper(String postId) {
    super("Comment access" + postId.hashCode());
    this.dao = DecadeDatabase.getInstance().getOrderCommentDao();
    this.commentAccess = new CommentAccess();
    this.handlerStore = CommentHandlerStore.getInstance();
    this.cAccessId = postId.hashCode();
    this.postId = postId;
    commentAccess.setId(cAccessId);
    commentAccess.setPostId(postId);
    dao.insertCommentAccess(commentAccess);
  }

  private HandlerAccess extractAccess(OrderedComment comment) {
    String commentId = comment.getCommentId();
    int accessId = comment.getAccessId();
    return handlerStore.getHandlerAccess(commentId, accessId);
  }

  @Override
  public List<HandlerAccess> loadFromLocal(Map<String, Object> query) {
    int length = (int) query.get("length");
    HandlerAccess handlerAccess = (HandlerAccess) query.get("last item");

    List<OrderedComment> comments = handlerAccess == null
            ? dao.findTopList(handlerAccess.getId(), length)
            : dao.findByOrder(handlerAccess.getId(), handlerAccess.getId(), length);
    List<HandlerAccess> items = new ArrayList<>();
    for (OrderedComment comment : comments) {
      items.add(extractAccess(comment));
    }
    return items;
  }

  private List<HandlerAccess> flushToLocal(List<CommentBody> bodies) {
    List<HandlerAccess> list = new ArrayList<>();
    for (CommentBody body : bodies) {
      Map<String, Object> itemPack = DtoConverter.convertToComment(body);
      Comment item = (Comment) itemPack.get("comment");
      HandlerAccess access = handlerStore.register(item, itemPack);

      OrderedComment ordered = new OrderedComment();
      ordered.setCommentAccessId(cAccessId);
      ordered.setCommentId(item.getId());
      ordered.setAccessId(access.getId());
      ordered.setOrd(item.getOrd());
      dao.insert(ordered);

      list.add(access);
    }
    return list;
  }

  @Override
  public Bundle loadFromServer() throws IOException {
    Bundle result = new Bundle();
    OrderedComment last = dao.findLast(commentAccess.getId());
    String lastId = null;
    if (last != null) {
      lastId = last.getCommentId();
    }
    Response<List<CommentBody>> res = HttpCallSupporter
            .create(CommentApi.class)
            .loadComment(postId, lastId)
            .execute();
    HttpCallSupporter.debug(res);
    List<CommentBody> bodies = res.body();

    flushToLocal(bodies);
    result.putInt("count loaded", bodies.size());
    return result;
  }

  @Override
  public List<HandlerAccess> update(Map<String, Object> data) throws IOException {
    List<CommentBody> bodies = (List<CommentBody>) data.get("items");
    return flushToLocal(bodies);
  }

  @Override
  public void cleanAll() {
    List<OrderedComment> comments = dao.findAll(commentAccess.getId());
    dao.deleteCommentAccess(cAccessId);
    for (OrderedComment comment : comments) {
      extractAccess(comment).release();
    }
  }
}
