package com.example.socialmediaapp.utils;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class TimeParserUtil {
   public static String parseTime(long time){
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      String timeDate = formatter.format(time);
      String oTime = timeDate.split(" ")[1];

      SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
      String df1 = dateFormatter.format(System.currentTimeMillis());
      String df2 = dateFormatter.format(time);
      String[] d1 = df1.split("-");
      String[] d2 = df2.split("-");
      if (!Objects.equals(d1[2], d2[2])) {
         return timeDate;
      }
      if (!Objects.equals(df1, df2)) {
         int month = Integer.parseInt(d2[1]);
         return d2[0] + " " + Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + oTime;
      }

      return oTime;
   }
}
