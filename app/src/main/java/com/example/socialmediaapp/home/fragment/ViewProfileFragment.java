package com.example.socialmediaapp.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.layoutviews.profile.SelfProfileView;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.layoutviews.profile.model.Configurer;
import com.example.socialmediaapp.layoutviews.profile.model.FriendProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.FriendRequestProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.RequestFriendProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.StrangerProfileConfigurer;
import com.example.socialmediaapp.viewmodel.HomePageViewModel;
import com.example.socialmediaapp.viewmodel.ViewProfileViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.models.user.profile.SelfProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.NotMeProfile;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.util.Objects;

public class ViewProfileFragment extends Fragment implements FragmentAnimation {

    private UserBasicInfo userBasicInfo;

    public ViewProfileFragment(UserBasicInfo userBasicInfo) {
        this.userBasicInfo = userBasicInfo;
    }


    //i store the this fragment state in activity saved state
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(ViewProfileViewModel.class);
    }

    private CircleButton avatarButton;
    private ViewGroup contentPanel;
    private TextView fullnameTextView;
    private ViewProfileViewModel viewModel;
    private View infoView;
    private ViewGroup root;
    private CustomSpinningView spinnerLoading;
    private ViewGroup postPanel;

    public ViewProfileViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            return null;
        }
        root = (ViewGroup) inflater.inflate(R.layout.fragment_view_profile, container, false);
        avatarButton = (CircleButton) root.findViewById(R.id.avatar_button);
        fullnameTextView = (TextView) root.findViewById(R.id.fullname);
        contentPanel = root.findViewById(R.id.content_panel);
        infoView = root.findViewById(R.id.information_panel);
        postPanel = root.findViewById(R.id.post_panel);
        spinnerLoading = (CustomSpinningView) root.findViewById(R.id.load_spinner);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        viewModel.getUserProfileInfo().observe(getViewLifecycleOwner(), new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfileInfo) {
                onLoadedUserInformation(userProfileInfo);
                viewModel.getListPost().getUpdateOnRepo().observe(getViewLifecycleOwner(), new Observer<Update<Post>>() {
                    @Override
                    public void onChanged(Update<Post> mutableLiveDataUpdate) {
                        if (mutableLiveDataUpdate == null) return;
                        if (mutableLiveDataUpdate.getOp() == Update.Op.ADD) {
                            PostItemView post = new PostItemView(ViewProfileFragment.this, viewModel.getListPost(), mutableLiveDataUpdate.getPos());
                            postPanel.addView(post, mutableLiveDataUpdate.getPos());
                        } else {
                            postPanel.removeViewAt(mutableLiveDataUpdate.getPos());
                        }
                    }
                });
                viewModel.loadPosts(getContext()).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        spinnerLoading.setVisibility(View.GONE);
                        contentPanel.removeView(spinnerLoading);
                        spinnerLoading = null;
                    }
                });
            }
        });
        initOnClick();
        initValues();

        return root;
    }

    private void startLoadProfile() {
        spinnerLoading.setVisibility(View.VISIBLE);
        HomePageViewModel vm = ((HomePage) getActivity()).getViewModel();
        String myAlias = vm.getUserInfo().getValue().getAlias();
        if (Objects.equals(userBasicInfo.getAlias(), myAlias)) {
            SelfProfile selfProfile = new SelfProfile(this, vm);
            viewModel.getUserProfileInfo().setValue(selfProfile);
            vm.getUserInfo().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
                @Override
                public void onChanged(UserInformation info) {
                    fullnameTextView.setText(info.getFullname());
                }
            });
            vm.getAvatarPost().observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
                @Override
                public void onChanged(ImagePost imagePost) {
                    if (imagePost == null) return;
                    avatarButton.setBackgroundContent(imagePost.getImage(), 0);
                }
            });
            return;
        }
        viewModel.loadProfile(getContext(), userBasicInfo.getAlias());
    }

    private void initValues() {
        avatarButton.setBackgroundContent(userBasicInfo.getAvatar(), 0);
        fullnameTextView.setText(userBasicInfo.getFullname());
    }

    private void onLoadedUserInformation(UserProfile user) {
        contentPanel.removeView(infoView);

        if (user instanceof SelfProfile) {
            infoView = new SelfProfileView(getContext(), (SelfProfile) user, this);
        } else {
            NotMeProfile notMeProfile = (NotMeProfile) user;
            NotMeProfileView notMeProfileView = new NotMeProfileView(getContext(), notMeProfile, this);
            Configurer configurer = null;
            switch (notMeProfile.getType()) {
                case "stranger":
                    configurer = new StrangerProfileConfigurer(notMeProfileView);
                    break;
                case "friend request":
                    configurer = new FriendRequestProfileConfigurer(notMeProfileView);
                    break;
                case "request friend":
                    configurer = new RequestFriendProfileConfigurer(notMeProfileView);
                    break;
                case "friend":
                    configurer = new FriendProfileConfigurer(notMeProfileView);
                    break;
            }
            configurer.allowActionLeft();
            configurer.allowActionRight();

            notMeProfileView.changeConfiguration(configurer);
            infoView = notMeProfileView;
        }
        contentPanel.addView(infoView, 0);
    }

    private void initOnClick() {

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