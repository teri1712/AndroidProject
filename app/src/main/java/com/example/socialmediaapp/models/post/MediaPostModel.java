package com.example.socialmediaapp.models.post;

import com.example.socialmediaapp.models.post.base.PostModel;

public class MediaPostModel extends PostModel {
    private String mediaId;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public MediaPostModel() {

    }
}
