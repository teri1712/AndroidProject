package com.example.socialmediaapp.home.fragment.animations;

public interface FragmentAnimation {
    public void performStart();
    public void performEnd(Runnable endAction);
}
