package com.example.socialmediaapp.home.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogoutInProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutInProgressFragment extends Fragment {

   public LogoutInProgressFragment() {
   }

   public static LogoutInProgressFragment newInstance() {
      LogoutInProgressFragment fragment = new LogoutInProgressFragment();
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   private CustomSpinningView loadSpinner;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.loading_layout, container, false);
      loadSpinner = view.findViewById(R.id.load_spinner);
      loadSpinner.setVisibility(View.VISIBLE);
      return view;
   }

   @Override
   public void onDestroy() {
      loadSpinner.setVisibility(View.GONE);
      super.onDestroy();
   }
}