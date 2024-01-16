package com.example.socialmediaapp.models.post;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.ImageSpec;

public class ReplyModel {
   private UserBasicInfoModel sender;
   private Integer id;
   private String content;
   private boolean liked;
   private Long time;
   private Integer countLike;
   private Integer order;
   private boolean mine;
   private ImageSpec imageSpec;
   private String imageUri;

   public UserBasicInfoModel getSender() {
      return sender;
   }

   public void setSender(UserBasicInfoModel sender) {
      this.sender = sender;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public boolean isLiked() {
      return liked;
   }

   public void setLiked(boolean liked) {
      this.liked = liked;
   }

   public Long getTime() {
      return time;
   }

   public void setTime(Long time) {
      this.time = time;
   }

   public Integer getCountLike() {
      return countLike;
   }

   public void setCountLike(Integer countLike) {
      this.countLike = countLike;
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
