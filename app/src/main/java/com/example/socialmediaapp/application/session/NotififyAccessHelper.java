package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.NotificationApi;
import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.api.entities.CommentNotificationBody;
import com.example.socialmediaapp.api.entities.NotificationBody;
import com.example.socialmediaapp.api.entities.NotificationPreset;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.NotificationDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.noification.CommentNotification;
import com.example.socialmediaapp.application.entity.noification.FriendRequestNotification;
import com.example.socialmediaapp.application.entity.noification.NotificationItem;
import com.example.socialmediaapp.application.entity.noification.NotifyDetails;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.noification.ReplyCommentNotification;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;
import com.example.socialmediaapp.models.NotificationModel;
import com.example.socialmediaapp.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class NotififyAccessHelper
        extends DataAccessHelper<NotificationModel>
        implements DataUpdateHelper<NotificationModel> {
  protected NotifyDetails notifyDetails;
  private final NotificationDao dao;
  private final DecadeDatabase db;

  public NotififyAccessHelper() {
    super("Notification");
    db = DecadeDatabase.getInstance();
    dao = db.getNotificationDao();
  }

  public NotificationItem insertNewNotification(NotificationBody body) throws IOException {
    String type = body.getType();

    UserBasicInfoBody userBody = body.getFrom();

    NotificationItem item = new NotificationItem();
    item.setId(body.getId());
    item.setAvatarUri(ImageUtils.imagePrefUrl + userBody.getAvatarId());
    item.setContent(body.getContent());
    item.setRead(body.getRead());
    item.setTime(body.getTime());
    item.setId(body.getId());
    item.setType(type);

    db.runInTransaction(() -> {
      dao.updateNotifyDetails(notifyDetails);
      dao.insert(item);
      switch (type) {
        case "comment": {
          CommentNotificationBody commentBody = body.getCommentBody();
          PostBody postBody = commentBody.getPostBody();
          Map<String, Object> postPack = DtoConverter.convertToPost(postBody);
          Post post = (Post) postPack.get("post");
          int accessId = PostHandlerStore.getInstance()
                  .register(post, postPack).getId();

          CommentNotification commentNotify = new CommentNotification();
          commentNotify.setNotiId(item.getId());
          commentNotify.setPostId(postBody.getId());
          commentNotify.setCommentId(commentBody.getCommentId());
          commentNotify.setAccessId(accessId);
          dao.insertCommentNotify(commentNotify);
          break;
        }
        case "friend-request": {
          FriendRequestNotification fReqNotify = new FriendRequestNotification();
          fReqNotify.setNotiId(item.getId());
          fReqNotify.setUserId(fReqNotify.getUserId());
          dao.insertFReqNotify(fReqNotify);
          break;
        }
      }
    });
    return item;
  }

  public void initAndLoadPreset() throws IOException {
    notifyDetails = dao.findNotifyDetails();

    if (notifyDetails == null) {
      notifyDetails = new NotifyDetails();
      Response<NotificationPreset> res = HttpCallSupporter
              .create(NotificationApi.class)
              .loadPreset().execute();

      HttpCallSupporter.debug(res);

      NotificationPreset body = res.body();
      notifyDetails.setCntUnRead(body.getCountUnRead());
      notifyDetails.setId((int) dao.insertNotifyDetails(notifyDetails));
      NotificationBody last = body.getLatestItem();
      if (last != null) {
        insertNewNotification(last);
      }
    }
  }

  @Override
  public List<NotificationModel> loadFromLocal(Map<String, Object> query) {
    NotificationModel lastItem = (NotificationModel) query.get("last item");
    long lastItemTime = lastItem == null ? Long.MAX_VALUE : lastItem.getTime();
    List<NotificationModel> res = new ArrayList<>();
    List<NotificationItem> items = dao.loadNotifyByTime(lastItemTime);

    for (NotificationItem item : items) {
      res.add(ModelConvertor.convertToNotifyModel(item));
    }
    return res;
  }


  @Override
  public Bundle loadFromServer() throws IOException {
    String lastId = dao.findLastInLocal();
    Call<List<NotificationBody>> messages = HttpCallSupporter
            .create(NotificationApi.class)
            .loadNotification(lastId);

    Response<List<NotificationBody>> res = messages.execute();
    List<NotificationBody> bodies = res.body();

    for (NotificationBody body : bodies) {
      insertNewNotification(body);
    }
    Bundle bundle = new Bundle();
    bundle.putInt("count loaded", bodies.size());
    return bundle;
  }

  public void updateRead() throws IOException {
    NotificationItem fItem = dao.findFirstInLocal();
    if (fItem == null) return;

    Response<ResponseBody> res = HttpCallSupporter.create(NotificationApi.class)
            .readNotification(fItem.getId())
            .execute();
    db.runInTransaction(() -> {
      notifyDetails.setCntUnRead(0);
      dao.updateNotifyDetails(notifyDetails);
      dao.updateAllRead();
    });
    HttpCallSupporter.debug(res);
  }
  @Override
  public void cleanAll() {
    dao.deleteNotifyDetails();
    dao.deleteAll();
  }

  @Override
  public List<NotificationModel> update(Map<String, Object> data) throws IOException {
    NotificationBody body = (NotificationBody) data.get("item");
    NotificationItem item = insertNewNotification(body);
    NotificationModel model = ModelConvertor.convertToNotifyModel(item);
    List<NotificationModel> list = new ArrayList<>();
    list.add(model);
    return list;
  }
}
