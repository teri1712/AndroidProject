package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.post.OrderPostDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.OrderedPost;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.post.PostAccess;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class PostAccessHelper
        extends DataAccessHelper<HandlerAccess> {
  private final PostAccess postAccess;
  private final long pAccessId;
  private final OrderPostDao dao;
  private final SequenceDao seqDao;
  private final PostHandlerStore handlerStore;

  public PostAccessHelper() {
    super("Post access" + Long.MAX_VALUE);
    this.seqDao = DecadeDatabase.getInstance().getSequenceDao();
    this.dao = DecadeDatabase.getInstance().getOrderPostDao();
    this.postAccess = new PostAccess();
    this.handlerStore = PostHandlerStore.getInstance();
    this.pAccessId = Long.MAX_VALUE;

    postAccess.setId(pAccessId);
    dao.insertPostAccess(postAccess);
  }

  private HandlerAccess extractAccess(OrderedPost post) {
    String postId = post.getPostId();
    int accessId = post.getAccessId();
    return handlerStore.getHandlerAccess(postId, accessId);
  }


  @Override
  public List<HandlerAccess> loadFromLocal(Map<String, Object> query) {
    int lb = Integer.MIN_VALUE;
    int length = (int) query.get("length");

    HandlerAccess access = (HandlerAccess) query.get("last item");
    if (access != null) {
      int accessId = access.getId();
      OrderedPost lastItem = dao.find(pAccessId, accessId);
      lb = lastItem.getId();
    }
    List<OrderedPost> posts = dao.findByBound(pAccessId, lb, length);
    List<HandlerAccess> items = new ArrayList<>();
    for (OrderedPost ord : posts) {
      items.add(extractAccess(ord));
    }
    return items;
  }

  @Override
  public Bundle loadFromServer() throws IOException {
    Bundle result = new Bundle();
    Response<List<PostBody>> res = HttpCallSupporter.create(PostApi.class).load().execute();
    HttpCallSupporter.debug(res);

    List<PostBody> posts = res.body();
    for (PostBody body : posts) {
      Map<String, Object> itemPack = DtoConverter.convertToPost(body);
      Post item = (Post) itemPack.get("post");

      HandlerAccess access = handlerStore.register(item, itemPack);
      OrderedPost orderedPost = new OrderedPost();
      orderedPost.setId(seqDao.getTailValue());
      orderedPost.setPostId(item.getId());
      orderedPost.setPostAccessId(pAccessId);
      orderedPost.setAccessId(access.getId());
      dao.insert(orderedPost);
    }
    result.putInt("count loaded", posts.size());
    return result;
  }

  @Override
  public void cleanAll() {
    List<OrderedPost> orderedPosts = dao.findAll(pAccessId);
    dao.deletePostAccess(pAccessId);

    for (OrderedPost post : orderedPosts) {
      extractAccess(post).release();
    }
  }

  @Override
  public void pop(HandlerAccess lastItem) {
    OrderedPost orderedPost = dao.find(pAccessId, lastItem.getId());
    List<OrderedPost> orderedPosts = dao.findByUpperBound(pAccessId, orderedPost.getId());
    for (OrderedPost ord : orderedPosts) {
      dao.delete(ord);
      extractAccess(ord).release();
    }
  }
}
