package com.example.socialmediaapp.home.fragment.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.SpinningFrame;
import com.example.socialmediaapp.customview.progress.PostLoading;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.List;

public class PostFragment extends Fragment {


    public PostFragment(DataAccessHandler<Post> handler) {
        this.handler = handler;
    }

    private DataAccessHandler<Post> handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new PostFragmentViewModel(handler);
    }

    private ViewGroup postPanel;
    private PostFragmentViewModel viewModel;
    private PostLoading postLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        postPanel = view.findViewById(R.id.posts_panel);
        postLoading = new PostLoading(getContext());


        initViewModel();
        return view;
    }

    public PostFragmentViewModel getViewModel() {
        return viewModel;
    }

    public void initViewModel() {

        viewModel.getLoadPostState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                performLoading();
            } else {
                finishLoading();
            }
        });

        viewModel.getPostUpdate().observe(getViewLifecycleOwner(), new Observer<Update<Post>>() {
            @Override
            public void onChanged(Update<Post> postUpdate) {
                Update.Op op = postUpdate.getOp();
                int pos = postUpdate.getPos();
                Post post = postUpdate.getItem();
                if (op == Update.Op.ADD) {
                    PostItemView postItemView = new PostItemView(PostFragment.this, viewModel.createPostSession(post), post);
                    if (pos == -1) {
                        postPanel.addView(postItemView);
                    } else {
                        postPanel.addView(postItemView, 0);
                    }
                } else {
                    postPanel.removeViewAt(pos);
                }
            }
        });

        viewModel.loadPosts();
    }

    private void performLoading() {
        postLoading.start();
        postPanel.addView(postLoading);
    }

    private void finishLoading() {
        postLoading.cancel();
        postPanel.removeView(postLoading);
    }

    public void onDestroyView() {
        super.onDestroyView();
        postLoading.cancel();
    }

}
