package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.ReplyCommentBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.dao.comment.OrderReplyDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.reply.OrderedReply;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.entity.reply.ReplyCommentAccess;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class ReplyCommentAccessHelper
        extends DataAccessHelper<HandlerAccess>
        implements DataUpdateHelper<HandlerAccess> {
  private final ReplyHandlerStore handlerStore;
  private final ReplyCommentAccess replyAccess;
  private final Comment comment;
  private final Integer rAccessId;
  private final OrderReplyDao dao;

  public ReplyCommentAccessHelper(Comment comment) {
    super("Reply access" + comment.hashCode());
    this.handlerStore = ReplyHandlerStore.getInstance();
    this.comment = comment;
    this.dao = DecadeDatabase.getInstance().getOrderReplyDao();
    this.rAccessId = comment.getId().hashCode();
    replyAccess = new ReplyCommentAccess();
    replyAccess.setId(rAccessId);
    replyAccess.setCommentId(comment.getId());
    dao.insertReplyAccess(replyAccess);
  }

  private HandlerAccess extractAccess(OrderedReply reply) {
    String replyId = reply.getReplyCommentId();
    int accessId = reply.getAccessId();
    return handlerStore.getHandlerAccess(replyId, accessId);
  }

  @Override
  public List<HandlerAccess> loadFromLocal(Map<String, Object> query) {
    int length = (int) query.get("length");
    int lb = Integer.MIN_VALUE;
    HandlerAccess access = (HandlerAccess) query.get("last item");
    if (access != null) {
      OrderedReply lastItem = dao.find(access.getId(), rAccessId);
      lb = lastItem.getOrd();
    }
    List<OrderedReply> comments = dao.findByBound(rAccessId, lb, length);
    List<HandlerAccess> items = new ArrayList<>();
    for (OrderedReply comment : comments) {
      items.add(extractAccess(comment));
    }
    return items;
  }

  private List<HandlerAccess> flushToLocal(List<ReplyCommentBody> bodies) {
    List<HandlerAccess> list = new ArrayList<>();
    for (ReplyCommentBody body : bodies) {
      Map<String, Object> itemPack = DtoConverter.convertToReply(body);
      ReplyComment item = (ReplyComment) itemPack.get("comment");

      HandlerAccess access = ReplyHandlerStore
              .getInstance()
              .register(item, itemPack);
      OrderedReply ord = new OrderedReply();
      ord.setReplyCommentId(body.getId());
      ord.setOrd(comment.getOrd());
      ord.setAccessId(access.getId());
      ord.setReplyCommentAccessId(rAccessId);

      dao.insert(ord);
      list.add(access);
    }
    return list;
  }

  @Override
  public Bundle loadFromServer() throws IOException {
    Bundle result = new Bundle();
    OrderedReply last = dao.findLast(replyAccess.getId());
    Response<List<ReplyCommentBody>> res = HttpCallSupporter
            .create(CommentApi.class)
            .loadReplyComment(comment.getId(), last == null ? null : last.getReplyCommentId())
            .execute();
    HttpCallSupporter.debug(res);
    List<ReplyCommentBody> bodies = res.body();
    flushToLocal(bodies);
    result.putInt("count loaded", bodies.size());
    return result;
  }

  @Override
  public List<HandlerAccess> update(Map<String, Object> data) throws IOException {
    List<ReplyCommentBody> bodies = (List<ReplyCommentBody>) data.get("items");
    return flushToLocal(bodies);
  }

  @Override
  public void cleanAll() {
    List<OrderedReply> replies = dao.findAll(replyAccess.getId());
    dao.deleteReplyAccess(replyAccess.getId());

    for (OrderedReply reply : replies) {
      extractAccess(reply).release();
    }
  }
}
