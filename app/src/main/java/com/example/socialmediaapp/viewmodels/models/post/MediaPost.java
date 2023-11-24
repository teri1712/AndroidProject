package com.example.socialmediaapp.viewmodels.models.post;

import android.content.Context;

import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

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
