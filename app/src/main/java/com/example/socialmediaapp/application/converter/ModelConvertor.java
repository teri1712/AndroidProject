package com.example.socialmediaapp.application.converter;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.NotificationDao;
import com.example.socialmediaapp.application.dao.message.MessageDao;
import com.example.socialmediaapp.application.dao.post.PostDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.noification.CommentNotification;
import com.example.socialmediaapp.application.entity.noification.NotificationItem;
import com.example.socialmediaapp.application.entity.post.ImagePost;
import com.example.socialmediaapp.application.entity.post.MediaPost;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.noification.NotifyDetails;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;
import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.application.entity.user.UserProfile;
import com.example.socialmediaapp.application.session.DeferredValue;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.UserPrincipal;
import com.example.socialmediaapp.models.NotificationModel;
import com.example.socialmediaapp.models.NotifyDetailsModel;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.messenger.message.IconMessageItemModel;
import com.example.socialmediaapp.models.messenger.message.ImageMessageItemModel;
import com.example.socialmediaapp.models.messenger.message.TextMessageItemModel;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.models.post.CommentModel;
import com.example.socialmediaapp.models.post.ImagePostModel;
import com.example.socialmediaapp.models.post.MediaPostModel;
import com.example.socialmediaapp.models.post.ReplyModel;
import com.example.socialmediaapp.models.post.base.PostModel;
import com.example.socialmediaapp.models.user.FriendRequestModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.models.user.profile.NotMeProfileModel;
import com.example.socialmediaapp.models.user.profile.SelfProfileModel;
import com.example.socialmediaapp.models.user.profile.base.ProfileModel;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;

import java.io.IOException;

public class ModelConvertor {

  public static PostModel convertToPostModel(Post post) {
    DecadeDatabase db = DecadeDatabase.getInstance();
    PostDao postDao = db.getPostDao();
    UserBasicInfoDao userBasicInfoDao = db.getUserBasicInfoDao();
    ImagePost imagePost = postDao.findImagePostByPostId(post.getId());
    MediaPost mediaPost = postDao.findMediaPostByPostId(post.getId());
    UserBasicInfo author = userBasicInfoDao.findUser(post.getUserInfoId());

    UserBasicInfoModel authorModel = convertToUserModel(author);

    PostModel postModel = null;
    String type = post.getType();
    if (type.startsWith("text")) {
      postModel = new PostModel();
    } else if (type.startsWith("image")) {
      ImagePostModel imagePostModel = new ImagePostModel();
      postModel = imagePostModel;

      ImageSpec spec = new ImageSpec(imagePost.getWidth(), imagePost.getHeight());
      imagePostModel.setImageSpec(spec);

      imagePostModel.setImageUri(ImageUtils.imagePrefUrl + imagePost.getMediaId());
    } else if (type.startsWith("video")) {
      MediaPostModel mediaPostModel = new MediaPostModel();
      postModel = mediaPostModel;
      mediaPostModel.setMediaId(mediaPost.getMediaId());
    }

    postModel.setId(post.getId());
    postModel.setType(type);
    postModel.setTime(post.getTime());
    postModel.setLiked(post.isLiked());
    postModel.setAuthor(authorModel);
    postModel.setContent(post.getContent());
    postModel.setLikeCount(post.getLikeCount());
    postModel.setShareCount(post.getShareCount());
    postModel.setCommentCount(post.getCommentCount());


    return postModel;
  }

  public static UserBasicInfoModel convertBodyToUserBasicInfoModel(UserBasicInfoBody u) {
    UserBasicInfoModel model = new UserBasicInfoModel();
    model.setFullname(u.getFullname());
    model.setId(u.getId());
    int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, DecadeApplication
            .getInstance()
            .getResources()
            .getDisplayMetrics());
    String imageUri = ImageUtils.imagePrefUrl + u.getAvatarId();
    model.setAvatarUri(imageUri);
    try {
      model.setScaled(DecadeApplication.getInstance()
              .picasso
              .load(imageUri)
              .resize(r, r).get());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return model;
  }

  public static UserBasicInfoModel convertToUserModel(UserBasicInfo u) {
    UserBasicInfoModel model = new UserBasicInfoModel();
    model.setFullname(u.getFullname());
    model.setAlias(u.getAlias());
    model.setId(u.getId());
    int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, DecadeApplication
            .getInstance()
            .getResources()
            .getDisplayMetrics());
    String imageUri = ImageUtils.imagePrefUrl + u.getAvatarId();
    model.setAvatarUri(imageUri);
    try {
      model.setScaled(DecadeApplication.getInstance()
              .picasso
              .load(imageUri)
              .resize(r, r).get());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return model;
  }

  public static MessageItemModel convertToMessageModel(MessageItem msg) {
    MessageItemModel msgModel = null;
    MessageDao messageDao = DecadeDatabase.getInstance().getMessageDao();
    String type = msg.getType();
    switch (type) {
      case "image":
        ImageMessageItemModel imageMsgModel = new ImageMessageItemModel();
        ImageMessageItem imageMsg = messageDao.loadImageMessage(msg.getId());

        DisplayMetrics displayMetrics = DecadeApplication.getInstance()
                .getResources()
                .getDisplayMetrics();
        int mW = displayMetrics.widthPixels / 2;
        int mH = displayMetrics.heightPixels / 3;
        ImageSpec spec = ImageUtils.calSpec(imageMsg.getWidth(), imageMsg.getHeight(), mW, mH);

        imageMsgModel.setImageSpec(spec);
        imageMsgModel.setImageUri(imageMsg.getImageUri());

        msgModel = imageMsgModel;
        break;
      case "icon":
        msgModel = new IconMessageItemModel();
        break;
      case "text":
        TextMessageItemModel textMessageModel = new TextMessageItemModel();
        TextMessageItem textMsg = messageDao.loadTextMessage(msg.getId());
        textMessageModel.setText(textMsg.getContent());

        msgModel = textMessageModel;
        break;
      default:
        assert false;
        break;
    }
    msgModel.setMsgId(msg.getId());
    msgModel.setUnCommitted(msg.getPendId() != null);
    msgModel.setTime(msg.getTime());
    msgModel.setType(msg.getType());
    msgModel.setChatId(msg.getChatId());
    msgModel.setMine(msg.getMine());
    return msgModel;
  }

  public static ReplyModel convertToReplyModel(ReplyComment reply) {
    UserBasicInfoDao dao = DecadeDatabase.getInstance().getUserBasicInfoDao();
    ReplyModel model = new ReplyModel();
    model.setId(model.getId());
    model.setTime(reply.getTime());
    String imageId = reply.getImageId();
    if (imageId != null) {
      int maxH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, DecadeApplication
              .getInstance()
              .getResources()
              .getDisplayMetrics());
      int maxW = DecadeApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
      ImageSpec spec = ImageUtils.calSpec(reply.getImageWidth(), reply.getImageHeight(), maxW, maxH);

      model.setImageSpec(spec);
      model.setImageUri(ImageUtils.imagePrefUrl + imageId);
    }
    model.setSender(convertToUserModel(dao.findUser(reply.getUserInfoId())));
    model.setContent(reply.getContent());
    model.setCountLike(reply.getLikeCount());
    model.setOrder(reply.getOrd());
    model.setMine(reply.isMine());
    return model;
  }

  public static NotifyDetailsModel convertToNotifyDetailsModel(NotifyDetails notifyDetails) {
    NotifyDetailsModel model = new NotifyDetailsModel();
    model.setCountUnRead(notifyDetails.getCntUnRead());
    return model;
  }

  public static ProfileModel convertToUserProfileModel(UserProfile profile) {
    ProfileModel model = (profile.getType().equals("self")) ? new SelfProfileModel() : new NotMeProfileModel();
    model.setId(profile.getId());
    model.setGender(profile.getGender());
    model.setBirthday(profile.getBirthday());
    model.setFullname(profile.getFullname());
    model.setAlias(profile.getAlias());
    ChatInfo chatInfo = new ChatInfo();
    chatInfo.setOther(profile.getId());
    DeferredValue<UserPrincipal> host = OnlineSessionHandler.getInstance().getUserPrincipal();
    chatInfo.setMe(host.get().getUserId());
    chatInfo.setChatId(profile.getChatId());
    chatInfo.setFullname(profile.getFullname());
    model.setChatInfo(chatInfo);

    if (model instanceof NotMeProfileModel) {
      NotMeProfileModel notMeProfile = (NotMeProfileModel) model;
      notMeProfile.setType(profile.getType());
    }
    return model;
  }

  public static CommentModel convertToCommentModel(Comment comment) {
    CommentModel model = new CommentModel();
    UserBasicInfoDao dao = DecadeDatabase.getInstance().getUserBasicInfoDao();
    model.setId(comment.getId());
    model.setTime(comment.getTime());
    String imageUri = comment.getImageUri();
    if (imageUri != null) {
      int maxH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, DecadeApplication
              .getInstance()
              .getResources()
              .getDisplayMetrics());
      int maxW = DecadeApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
      ImageSpec spec = ImageUtils.calSpec(comment.getImageWidth(), comment.getImageHeight(), maxW, maxH);

      model.setImageSpec(spec);
      model.setImageUri(imageUri);
    }
    model.setAuthor(convertToUserModel(dao.findUser(comment.getUserInfoId())));
    model.setContent(comment.getContent());
    model.setCountLike(comment.getLikeCount());
    model.setOrder(comment.getOrd());
    return model;
  }

  public static NotificationModel convertToNotifyModel(NotificationItem item) {
    NotificationDao dao = DecadeDatabase.getInstance().getNotificationDao();
    NotificationModel model = new NotificationModel();

    model.setAvatarUri(item.getAvatarUri());
    model.setContent(item.getContent());
    model.setTime(item.getTime());

    Bundle action = new Bundle();
    String type = item.getType();

    action.putString("type", type);
    switch (type) {
      case "comment":
        CommentNotification commentNotify
                = dao.findCommentNotifyByNotiId(item.getId());

        action.putString("comment id", commentNotify.getCommentId());
        action.putString("post id", commentNotify.getPostId());
        action.putInt("access id", commentNotify.getAccessId());
        break;
      case "friend-request":
        action.putString("user id", dao
                .findFReqNotifyByNotiId(item.getId())
                .getUserId());
        break;
    }
    model.setAction(action);
    return model;
  }

  public static FriendRequestModel convertToFReq(FriendRequestItem item) {
    UserBasicInfoDao userDao = DecadeDatabase.getInstance().getUserBasicInfoDao();
    UserBasicInfo user = userDao.findUser(item.getUserInfoId());

    UserBasicInfoModel userModel = ModelConvertor.convertToUserModel(user);
    FriendRequestModel fReqModel = new FriendRequestModel();
    fReqModel.setTime(item.getTime());
    fReqModel.setUserModel(userModel);
    return fReqModel;
  }
}
