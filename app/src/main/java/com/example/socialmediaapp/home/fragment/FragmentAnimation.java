package com.example.socialmediaapp.home.fragment;

public interface FragmentAnimation {
    void performStart();
    void performEnd(Runnable endAction);
}
