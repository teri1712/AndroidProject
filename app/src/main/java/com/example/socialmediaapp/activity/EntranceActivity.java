package com.example.socialmediaapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.UserPrincipal;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;

public class EntranceActivity extends AppCompatActivity {
   private CustomSpinningView loadSpinner;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.loading_layout);
      loadSpinner = findViewById(R.id.load_spinner);
      loadSpinner.setVisibility(View.VISIBLE);
      OnlineSessionHandler onlineSessionHandler = DecadeApplication.getInstance().onlineSessionHandler;
      LiveData<UserPrincipal> userPrincipalLiveData = onlineSessionHandler.getPrincipalLiveData();
      userPrincipalLiveData.observe(this, userPrincipal -> {
         loadSpinner.setVisibility(View.GONE);
         Intent intent;
         if (userPrincipal == null) {
            intent = new Intent(EntranceActivity.this, LoginFormActivity.class);
         } else {
            intent = new Intent(EntranceActivity.this, HomePage.class);
         }
         startActivity(intent);
         finish();
      });
   }


   @Override
   protected void onDestroy() {
      if (loadSpinner.getVisibility() == View.VISIBLE) {
         loadSpinner.setVisibility(View.GONE);
      }
      super.onDestroy();
   }
}