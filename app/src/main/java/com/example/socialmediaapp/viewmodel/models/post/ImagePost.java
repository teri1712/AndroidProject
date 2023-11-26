package com.example.socialmediaapp.viewmodel.models.post;

import android.graphics.Bitmap;

import com.example.socialmediaapp.viewmodel.models.post.base.Post;

public class ImagePost extends Post {
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ImagePost() {
        super();
    }
}
