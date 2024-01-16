package com.example.socialmediaapp.home.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.utils.ImageUtils;

public class ViewImageFragment extends Fragment {

   public static ViewImageFragment newInstance(String imageUri) {

      Bundle args = new Bundle();
      args.putString("image uri", imageUri);
      ViewImageFragment fragment = new ViewImageFragment();
      fragment.setArguments(args);
      return fragment;
   }

   public ViewImageFragment() {
   }

   private String imageuri;
   private ImageView imageView;
   private LiveData<Bitmap> imageLiveData;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Bundle arg = getArguments();
      imageuri = arg.getString("image uri");
      imageLiveData = ImageUtils.load(imageuri);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      imageView = (ImageView) inflater.inflate(R.layout.view_image_layout, container, false);
      imageView.setClickable(true);
      imageLiveData.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
         @Override
         public void onChanged(Bitmap bitmap) {
            imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
         }
      });
      return imageView;
   }

   public void onStart() {
      super.onStart();
      imageView.animate().alpha(1).setDuration(100).start();
   }
}