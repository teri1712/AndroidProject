package com.example.socialmediaapp.viewmodel.models.messenger;

import android.graphics.Bitmap;

public class ImageMessageItem extends MessageItem {
    private Bitmap bitmap;

    public ImageMessageItem() {

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
