package com.example.socialmediaapp.models.messenger.message;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.utils.ImageSpec;

public class ImageMessageItemModel extends MessageItemModel {
   private ImageSpec imageSpec;
   private String imageUri;

   public String getImageUri() {
      return imageUri;
   }

   public void setImageUri(String imageUri) {
      this.imageUri = imageUri;
   }

   public ImageSpec getImageSpec() {
      return imageSpec;
   }

   public void setImageSpec(ImageSpec imageSpec) {
      this.imageSpec = imageSpec;
   }

   public ImageMessageItemModel() {

   }

}
