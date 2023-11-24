package com.example.socialmediaapp.activitiy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.socialmediaapp.R;

public class PersonalWallActivity extends AppCompatActivity {


    private String userAlias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_profile);
        userAlias = getIntent().getStringExtra("user alias");
    }
}