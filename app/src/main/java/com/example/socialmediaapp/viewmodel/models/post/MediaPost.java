package com.example.socialmediaapp.viewmodel.models.post;

import com.example.socialmediaapp.viewmodel.models.post.base.Post;

public class MediaPost extends Post {
    private Integer mediaId;

    public MediaPost() {

    }
    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }
}
