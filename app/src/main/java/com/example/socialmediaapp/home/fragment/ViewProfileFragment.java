package com.example.socialmediaapp.home.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.PostLoading;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.layoutviews.profile.SelfProfileView;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.layoutviews.profile.model.Configurer;
import com.example.socialmediaapp.layoutviews.profile.model.FriendProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.FriendRequestProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.RequestFriendProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.StrangerProfileConfigurer;
import com.example.socialmediaapp.viewmodel.ViewProfileViewModel;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.user.profile.SelfProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.NotMeProfile;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.util.List;

public class ViewProfileFragment extends Fragment implements FragmentAnimation {

    private UserBasicInfo userBasicInfo;

    public ViewProfileFragment() {
    }

    public ViewProfileFragment(UserBasicInfo userBasicInfo) {
        this.userBasicInfo = userBasicInfo;
    }

    public static ViewProfileFragment newInstance(Bundle args, UserBasicInfo userBasicInfo) {
        ViewProfileFragment fragment = new ViewProfileFragment(userBasicInfo);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        viewModel = new ViewProfileViewModel(args.getInt("session id"));
    }

    private CircleButton avatarButton;
    private ViewGroup contentPanel;
    private TextView fullnameTextView;
    private ViewProfileViewModel viewModel;
    private View infoView;
    private ViewGroup root;
    private CustomSpinningView spinnerLoading;

    public ViewProfileViewModel getViewModel() {
        return viewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = (ViewGroup) inflater.inflate(R.layout.fragment_view_profile, container, false);
        avatarButton = root.findViewById(R.id.avatar_button);
        fullnameTextView = root.findViewById(R.id.fullname);
        contentPanel = root.findViewById(R.id.content_panel);
        infoView = root.findViewById(R.id.information_panel);
        spinnerLoading = root.findViewById(R.id.load_spinner);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        initValues();
        return root;
    }


    private void startLoadProfile() {
        spinnerLoading.setVisibility(View.VISIBLE);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                ImagePost avatarPost = userProfile.getAvatarPost();
                ImagePost backgroundPost = userProfile.getBackgroundPost();
                avatarButton.setBackgroundContent(avatarPost == null ? null : new BitmapDrawable(getResources(), avatarPost.getImage()), 0);
                onLoadedUserInformation(userProfile, (ViewProfileSessionHandler) viewModel.getViewProfileSessionHandler().getValue());
            }
        });
        viewModel.getUserPostsSession().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                spinnerLoading.setVisibility(View.GONE);
                PostFragment postFragment = new PostFragment((DataAccessHandler<Post>) sessionHandler);
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.post_fragment_container, postFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void initValues() {
        if (userBasicInfo == null) return;
        Bitmap avt = userBasicInfo.getAvatar();
        avatarButton.setBackgroundContent(avt == null ? null : new BitmapDrawable(getResources(), avt), 0);
        fullnameTextView.setText(userBasicInfo.getFullname());
    }

    private void onLoadedUserInformation(UserProfile user, ViewProfileSessionHandler handler) {
        contentPanel.removeView(infoView);

        if (user instanceof SelfProfile) {
            infoView = new SelfProfileView(this);
        } else {
            NotMeProfile notMeProfile = (NotMeProfile) user;
            NotMeProfileView notMeProfileView = new NotMeProfileView(this);
            Configurer configurer = null;
            switch (notMeProfile.getType()) {
                case "stranger":
                    configurer = new StrangerProfileConfigurer(notMeProfileView, handler);
                    break;
                case "friend request":
                    configurer = new FriendRequestProfileConfigurer(notMeProfileView, handler);
                    break;
                case "request friend":
                    configurer = new RequestFriendProfileConfigurer(notMeProfileView, handler);
                    break;
                case "friend":
                    configurer = new FriendProfileConfigurer(notMeProfileView, handler);
                    break;
            }
            configurer.allowActionLeft();
            configurer.allowActionRight();

            notMeProfileView.changeConfiguration(configurer);
            infoView = notMeProfileView;
        }
        contentPanel.addView(infoView, 0);
    }

    @Override
    public void performEnd(Runnable endAction) {
        root.animate().translationX(root.getWidth()).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {
                endAction.run();
            }
        }).start();
    }

    @Override
    public void performStart() {
        root.setTranslationX(((View) root.getParent()).getWidth() * 66 / 100);
        root.animate().translationX(0).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {
                startLoadProfile();
            }
        }).start();
    }
}