package com.example.socialmediaapp.api.entities;



public class PostBody {
    private String id;
    private String status;
    private String type;
    private Integer likeCount, commentCount, shareCount = 0;
    private Long time;
    private Boolean liked;
    private UserBasicInfoBody author;
    private ImageBody imageBody;
    private MediaBody mediaBody;

    public PostBody() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public UserBasicInfoBody getAuthor() {
        return author;
    }

    public void setAuthor(UserBasicInfoBody author) {
        this.author = author;
    }

    public ImageBody getImageBody() {
        return imageBody;
    }

    public void setImageBody(ImageBody imageBody) {
        this.imageBody = imageBody;
    }

    public MediaBody getMediaBody() {
        return mediaBody;
    }

    public void setMediaBody(MediaBody mediaBody) {
        this.mediaBody = mediaBody;
    }
}
