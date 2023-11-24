package com.example.socialmediaapp.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.home.fragment.SetUpInformationFragment;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.test_container, SetUpInformationFragment.newInstance(), null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}