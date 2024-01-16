package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.post.OrderPostDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.OrderedPost;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.post.PostAccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class UserPostAccessHelper extends DataAccessHelper<HandlerAccess> {
  private final DecadeDatabase db;
  private final SequenceDao seqDao;
  private final PostAccess postAccess;
  private final OrderPostDao dao;
  private final long pAccessId;
  private final String userId;
  private final PostHandlerStore handlerStore;

  public UserPostAccessHelper(String userId) {
    super("Post access" + userId.hashCode());
    this.userId = userId;
    this.db = DecadeDatabase.getInstance();
    this.dao = db.getOrderPostDao();
    this.seqDao = db.getSequenceDao();
    this.postAccess = new PostAccess();
    this.pAccessId = userId.hashCode();
    this.handlerStore = PostHandlerStore.getInstance();

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
    int length = (int) query.get("length");
    int lb = Integer.MIN_VALUE;
    HandlerAccess access = (HandlerAccess) query.get("last item");
    if (access != null) {
      OrderedPost lastItem = dao.find(pAccessId, access.getId());
      lb = lastItem.getId();
    }
    List<OrderedPost> posts = dao.findByBound(pAccessId, lb, length);
    List<HandlerAccess> res = new ArrayList<>();
    for (OrderedPost post : posts) {
      res.add(extractAccess(post));
    }
    return res;
  }

  @Override
  public Bundle loadFromServer() throws IOException {
    Bundle result = new Bundle();
    OrderedPost last = dao.findLast(pAccessId);
    Response<List<PostBody>> res = HttpCallSupporter.create(PostApi.class)
            .loadPostsOfUser(userId, last == null ? null : last.getPostId())
            .execute();
    HttpCallSupporter.debug(res);
    List<PostBody> bodies = res.body();
    for (PostBody body : bodies) {
      Map<String, Object> itemPack = DtoConverter.convertToPost(body);
      Post item = (Post) itemPack.get("post");
      HandlerAccess access = PostHandlerStore
              .getInstance()
              .register(item, itemPack);
      OrderedPost orderedPost = new OrderedPost();
      orderedPost.setPostId(item.getId());
      orderedPost.setId(seqDao.getTailValue());
      orderedPost.setPostAccessId(pAccessId);
      orderedPost.setAccessId(access.getId());

      dao.insert(orderedPost);
    }
    result.putInt("count loaded", bodies.size());
    return result;
  }

  @Override
  public void cleanAll() {
    List<OrderedPost> posts = dao.findAll(pAccessId);
    dao.deletePostAccess(pAccessId);
    for (OrderedPost post : posts) {
      extractAccess(post).release();
    }
  }
}
