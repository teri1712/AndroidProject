package com.example.socialmediaapp.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.MutableLiveData;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class LiveDataBitmapTarget implements Target {
   private MutableLiveData<Bitmap> bitmap;

   public LiveDataBitmapTarget(MutableLiveData<Bitmap> bitmap) {
      this.bitmap = bitmap;
   }

   @Override
   public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      this.bitmap.setValue(bitmap);
   }

   @Override
   public void onBitmapFailed(Exception e, Drawable errorDrawable) {
      // will implement later
      e.printStackTrace();
   }

   @Override
   public void onPrepareLoad(Drawable placeHolderDrawable) {

   }
}
