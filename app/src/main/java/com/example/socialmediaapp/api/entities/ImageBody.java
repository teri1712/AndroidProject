package com.example.socialmediaapp.api.entities;

public class ImageBody {
   private String mediaId;
   private Integer width;
   private Integer height;
   public ImageBody() {
   }

   public Integer getWidth() {
      return width;
   }

   public void setWidth(Integer width) {
      this.width = width;
   }

   public Integer getHeight() {
      return height;
   }

   public void setHeight(Integer height) {
      this.height = height;
   }

   public String getMediaId() {
      return mediaId;
   }

   public void setMediaId(String mediaId) {
      this.mediaId = mediaId;
   }
}
