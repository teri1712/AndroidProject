package com.example.socialmediaapp.api.entities;

public class CommentBody {
  private UserBasicInfoBody author;
  private String id;
  private String postId;
  private String content;
  private boolean liked;
  private Integer countLike;
  private Integer countReply;
  private Integer order;
  private boolean mine;
  private ImageBody imageBody;
  private Long time;

  public UserBasicInfoBody getAuthor() {
    return author;
  }

  public void setAuthor(UserBasicInfoBody author) {
    this.author = author;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public Integer getCountLike() {
    return countLike;
  }

  public void setCountLike(Integer countLike) {
    this.countLike = countLike;
  }

  public Integer getCountReply() {
    return countReply;
  }

  public void setCountReply(Integer countReply) {
    this.countReply = countReply;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public boolean isMine() {
    return mine;
  }

  public void setMine(boolean mine) {
    this.mine = mine;
  }

  public ImageBody getImageBody() {
    return imageBody;
  }

  public void setImageBody(ImageBody imageBody) {
    this.imageBody = imageBody;
  }
}
