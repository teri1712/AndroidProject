package com.example.socialmediaapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.layoutviews.items.message.TextingAnimateView;

public class TestActivity extends AppCompatActivity {
   private TextingAnimateView animateView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.message_texting);
      animateView = findViewById(R.id.texting_view);
   }

   @Override
   protected void onStart() {
      super.onStart();
      animateView.performAnimation();
   }
}