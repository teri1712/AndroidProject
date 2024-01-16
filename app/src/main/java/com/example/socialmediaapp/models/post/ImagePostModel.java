package com.example.socialmediaapp.models.post;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.models.post.base.PostModel;
import com.example.socialmediaapp.utils.ImageSpec;

public class ImagePostModel extends PostModel {
    private ImageSpec imageSpec;
    private String imageUri;

    public ImagePostModel() {
        super();
    }

    public ImageSpec getImageSpec() {
        return imageSpec;
    }

    public void setImageSpec(ImageSpec imageSpec) {
        this.imageSpec = imageSpec;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
