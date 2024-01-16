package com.example.socialmediaapp.models.post;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.ImageSpec;

public class CommentModel {
   private String id;
   private UserBasicInfoModel author;
   private String content;
   private boolean liked;
   private Long time;
   private Integer countLike;
   private Integer order;
   private boolean mine;
   private String imageUri;
   private ImageSpec imageSpec;

   public boolean isLiked() {
      return liked;
   }

   public void setLiked(boolean liked) {
      this.liked = liked;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }


   public Integer getCountLike() {
      return countLike;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public Integer getOrder() {
      return order;
   }

   public void setOrder(Integer order) {
      this.order = order;
   }

   public boolean isMine() {
      return mine;
   }

   public void setMine(boolean mine) {
      this.mine = mine;
   }

   public void setCountLike(Integer countLike) {
      this.countLike = countLike;
   }

   public CommentModel() {
   }

   public UserBasicInfoModel getAuthor() {
      return author;
   }

   public String getImageUri() {
      return imageUri;
   }

   public void setImageUri(String imageUri) {
      this.imageUri = imageUri;
   }

   public void setAuthor(UserBasicInfoModel author) {
      this.author = author;
   }


   public ImageSpec getImageSpec() {
      return imageSpec;
   }

   public void setImageSpec(ImageSpec imageSpec) {
      this.imageSpec = imageSpec;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }


}
