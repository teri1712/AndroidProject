package com.example.socialmediaapp.application.converter;

import android.graphics.Bitmap;
import android.util.ArrayMap;

import androidx.lifecycle.MediatorLiveData;

import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.api.entities.FriendRequestBody;
import com.example.socialmediaapp.api.entities.ImageBody;
import com.example.socialmediaapp.api.entities.MediaBody;
import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.api.entities.ReplyCommentBody;
import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.api.entities.UserProfileBody;
import com.example.socialmediaapp.api.entities.UserSessionBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.message.IconMessageItem;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.post.ImagePost;
import com.example.socialmediaapp.application.entity.post.MediaPost;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;
import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.application.entity.user.UserProfile;
import com.example.socialmediaapp.models.UserSession;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.user.UserInformation;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.utils.LiveDataBitmapTarget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DtoConverter {
  public static Map<String, Object> convertToPost(PostBody postBody) {
    Map<String, Object> itemPack = new HashMap<>();
    Post p = new Post();

    String type = postBody.getType();
    p.setId(postBody.getId());
    p.setType(type);
    p.setTime(postBody.getTime());
    p.setLiked(postBody.getLiked());
    p.setContent(postBody.getStatus());
    p.setLikeCount(postBody.getLikeCount());
    p.setShareCount(postBody.getShareCount());
    p.setCommentCount(postBody.getCommentCount());

    if (type.startsWith("image")) {
      ImageBody imageBody = postBody.getImageBody();
      ImagePost imagePost = new ImagePost();
      imagePost.setMediaId(imageBody.getMediaId());
      imagePost.setWidth(imageBody.getWidth());
      imagePost.setHeight(imageBody.getHeight());
      itemPack.put("image post", imagePost);
    } else if (type.startsWith("video")) {
      MediaBody mediaBody = postBody.getMediaBody();
      MediaPost mediaPost = new MediaPost();
      mediaPost.setMediaId(mediaBody.getMediaId());
      itemPack.put("media post", mediaPost);
    }
    itemPack.put("post", p);
    itemPack.put("user basic info", convertToUserBasicInfo(postBody.getAuthor()));
    return itemPack;
  }

  public static Map<String, Object> convertToComment(CommentBody commentBody) {
    Map<String, Object> m = new ArrayMap<>();
    Comment comment = new Comment();

    comment.setId(commentBody.getId());
    comment.setMine(commentBody.isMine());
    comment.setOrd(commentBody.getOrder());
    comment.setTime(commentBody.getTime());
    comment.setLiked(commentBody.isLiked());
    comment.setContent(commentBody.getContent());
    comment.setCommitted(true);
    ImageBody imageBody = commentBody.getImageBody();
    if (imageBody != null) {
      comment.setImageUri(DecadeApplication.localhost + imageBody.getMediaId());
      comment.setImageWidth(imageBody.getWidth());
      comment.setImageHeight(imageBody.getHeight());
    }
    comment.setLikeCount(commentBody.getCountLike());
    comment.setCountReply(commentBody.getCountReply());

    m.put("comment", comment);
    m.put("user basic info", convertToUserBasicInfo(commentBody.getAuthor()));
    return m;
  }

  public static Map<String, Object> convertToReply(ReplyCommentBody replyBody) {
    Map<String, Object> m = new HashMap<>();

    ReplyComment reply = new ReplyComment();
    reply.setId(replyBody.getId());
    reply.setTime(replyBody.getTime());
    reply.setLiked(replyBody.isLiked());
    reply.setContent(replyBody.getContent());
    ImageBody imageBody = replyBody.getImageBody();
    if (imageBody != null) {
      reply.setImageId(imageBody.getMediaId());
      reply.setImageWidth(imageBody.getWidth());
      reply.setImageHeight(imageBody.getHeight());
    }
    reply.setLikeCount(replyBody.getCountLike());
    reply.setMine(replyBody.isMine());
    reply.setOrd(replyBody.getOrder());

    m.put("comment", reply);
    m.put("user basic info", convertToUserBasicInfo(replyBody.getAuthor()));
    return m;
  }

  public static UserSession convertToUserSession(UserSessionBody body) throws IOException {
    UserInformation info = new UserInformation();

    info.setId(body.getUserInfo().getId());
    info.setAlias(body.getUserInfo().getAlias());
    info.setGender(body.getUserInfo().getGender());
    info.setBirthday(body.getUserInfo().getBirthday());
    info.setFullname(body.getUserInfo().getFullname());

    UserSession userSession = new UserSession();
    userSession.setUserInfo(info);

    String avtUri = ImageUtils.imagePrefUrl + body.getAvatarId();
    MediatorLiveData<Bitmap> avatar = new MediatorLiveData<>();
    ImageUtils.loadInto(avtUri, new LiveDataBitmapTarget(avatar));
    userSession.setAvatarUri(avtUri);
    userSession.setAvatar(avatar);

    String bgUri = ImageUtils.imagePrefUrl + body.getBackgroundId();
    MediatorLiveData<Bitmap> bg = new MediatorLiveData<>();
    ImageUtils.loadInto(bgUri, new LiveDataBitmapTarget(bg));
    userSession.setBgUri(bgUri);
    userSession.setBackground(bg);

    return userSession;
  }

  public static UserProfile convertToUserProfile(UserProfileBody userProfileBody) {
    UserProfile userProfile = new UserProfile();
    userProfile.setId(userProfileBody.getId());
    userProfile.setType(userProfileBody.getType());
    userProfile.setAlias(userProfileBody.getAlias());
    userProfile.setGender(userProfileBody.getGender());
    userProfile.setChatId(userProfileBody.getChatInfo().getChatId());
    PostBody bg = userProfileBody.getBackgroundPost();
    PostBody avt = userProfileBody.getAvatarPost();
    userProfile.setBackgroundPostId(bg == null ? null : bg.getId());
    userProfile.setAvatarPostId(avt == null ? null : avt.getId());
    userProfile.setBirthday(userProfileBody.getBirthday());
    userProfile.setFullname(userProfileBody.getFullname());

    return userProfile;
  }

  public static UserBasicInfo convertToUserBasicInfo(UserBasicInfoBody userBasicInfoBody) {
    UserBasicInfo userBasicInfo = new UserBasicInfo();
    userBasicInfo.setFullname(userBasicInfoBody.getFullname());
    userBasicInfo.setId(userBasicInfoBody.getId());
    userBasicInfo.setAlias(userBasicInfo.getAlias());
    userBasicInfo.setAvatarId(userBasicInfoBody.getAvatarId());
    return userBasicInfo;
  }


  public static Map<String, Object> convertToMessageItem(MessageItemBody body) {
    Map<String, Object> m = new ArrayMap<>();
    ChatInfo chatInfo = body.getChatInfo();

    MessageItem messageItem = new MessageItem();
    m.put("message item", messageItem);

    messageItem.setChatId(chatInfo.getChatId());
    messageItem.setMessageId(body.getId());
    messageItem.setOrd(body.getOrd());
    messageItem.setMine(body.getSender().equals(chatInfo.getMe()));
    messageItem.setTime(body.getTime());
    String type = body.getType();
    messageItem.setType(type);
    switch (type) {
      case "image": {
        String imageId = body.getImageBody().getMediaId();
        int width = body.getImageBody().getWidth();
        int height = body.getImageBody().getHeight();
        ImageMessageItem imageMessageItem = new ImageMessageItem();
        imageMessageItem.setImageUri(ImageUtils.imagePrefUrl + imageId);
        imageMessageItem.setWidth(width);
        imageMessageItem.setHeight(height);
        m.put("image message item", imageMessageItem);
        break;
      }
      case "icon": {
        IconMessageItem iconMessageItem = new IconMessageItem();
        m.put("icon message item", iconMessageItem);
        break;
      }
      case "text": {
        TextMessageItem textMessageItem = new TextMessageItem();
        textMessageItem.setContent(body.getContent());
        m.put("text message item", textMessageItem);
        break;
      }
      default:
        assert false;
        break;
    }
    return m;
  }

  public static FriendRequestItem convertToFReq(FriendRequestBody body) {
    FriendRequestItem fReqItem = new FriendRequestItem();
    fReqItem.setId(body.getId());
    fReqItem.setTime(body.getTime());
    return fReqItem;
  }

  private static Bitmap getImageBitmap(String imageId) throws IOException {
    return DecadeApplication.getInstance().picasso.load(ImageUtils.imagePrefUrl + imageId).get();
  }

}
