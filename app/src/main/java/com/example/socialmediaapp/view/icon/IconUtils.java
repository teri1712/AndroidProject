package com.example.socialmediaapp.view.icon;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.TypedValue;

import androidx.core.graphics.PathParser;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.DecadeApplication;

import org.xmlpull.v1.XmlPullParser;

public class IconUtils {
   private static Path likePath;

   public static Path loadLikePath() {
      if (likePath != null) return new Path(likePath);
      likePath = new Path();
      Resources res = DecadeApplication.getInstance().getResources();
      try {
         // Parse the VectorDrawable XML file
         XmlResourceParser xrp = res.getXml(R.drawable.messeger_like);
         int eventType = xrp.getEventType();
         while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
               if (xrp.getName().equals("path")) {
                  String pathData = xrp.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData");
                  Path path = PathParser.createPathFromPathData(pathData);
                  likePath.addPath(path);
               }
            }
            eventType = xrp.next();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      RectF bounds = new RectF();
      likePath.computeBounds(bounds, true);


      Matrix matrix = new Matrix();
      float r = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, res.getDisplayMetrics());


      float scale = r / (bounds.width());
      matrix.setScale(scale, scale);
      likePath.transform(matrix);

      return new Path(likePath);
   }
}
