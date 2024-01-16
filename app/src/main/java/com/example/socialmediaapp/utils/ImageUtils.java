package com.example.socialmediaapp.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.DecadeApplication;

import java.io.FileNotFoundException;


/* NOTE : Using picasso to download and cache
 *
 * */

public class ImageUtils {
  public static final String imagePrefUrl = DecadeApplication.localhost + "/media/image/";

  public static Bitmap scale(Bitmap image, int maxW, int maxH) {
    if (image == null) return null;
    int w = image.getWidth(), h = image.getHeight();
    if (w > maxW) {
      float sc = (float) maxW / w;
      w = maxW;
      h = (int) (h * sc);
    }
    if (h > maxH) {
      float sc = (float) maxH / h;
      h = maxH;
      w = (int) (w * sc);
    }
    Bitmap scaled = Bitmap.createScaledBitmap(image, w, h, true);
    return scaled;
  }

  public static ImageSpec calSpec(int w, int h, int maxW, int maxH) {
    if (w > maxW) {
      float sc = (float) maxW / w;
      w = maxW;
      h = (int) (h * sc);
    }
    if (h > maxH) {
      float sc = (float) maxH / h;
      h = maxH;
      w = (int) (w * sc);
    }
    return new ImageSpec(w, h);
  }

  public static LiveData<Bitmap> loadWithSpec(ImageSpec spec, String uriPath) {
    MutableLiveData<Bitmap> target = new MutableLiveData<>();
    DecadeApplication.getInstance().picasso.load(uriPath).resize(spec.w, spec.h).into(new LiveDataBitmapTarget(target));
    return target;
  }


  public static LiveData<Bitmap> load(String imageUri) {
    MutableLiveData<Bitmap> target = new MutableLiveData<>();
    DecadeApplication.getInstance().picasso.load(imageUri).into(new LiveDataBitmapTarget(target));
    return target;
  }

  public static void loadInto(String imageUri, LiveDataBitmapTarget target) {
    DecadeApplication.getInstance().picasso.load(imageUri).into(target);
  }

  public static Bitmap cropCircle(Bitmap bitmap) {
    if (bitmap == null) return null;
    int w = bitmap.getWidth(), h = bitmap.getHeight();
    Bitmap cropped = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(cropped);
    Path path = new Path();
    path.addCircle(w / 2, h / 2, w / 2, Path.Direction.CW);
    canvas.clipPath(path);
    canvas.drawBitmap(bitmap, 0, 0, null);
    return cropped;
  }

  public static ImageSpec calSpecFromFile(Uri uri) {
    ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inJustDecodeBounds = true;
    try {
      BitmapFactory.decodeStream(resolver.openInputStream(uri), null, opt);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
    return new ImageSpec(opt.outWidth, opt.outHeight);
  }
}
