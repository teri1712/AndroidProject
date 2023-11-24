package com.example.socialmediaapp.viewmodels.models.post;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;

import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

public class ImagePost extends Post {
    private Drawable image;

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public ImagePost() {
        super();
    }
}
