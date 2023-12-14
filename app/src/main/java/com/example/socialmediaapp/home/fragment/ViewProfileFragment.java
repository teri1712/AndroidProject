package com.example.socialmediaapp.home.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.layoutviews.profile.SelfProfileView;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.layoutviews.profile.base.ProfileView;
import com.example.socialmediaapp.layoutviews.profile.model.Configurer;
import com.example.socialmediaapp.layoutviews.profile.model.FriendProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.FriendRequestProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.RequestFriendProfileConfigurer;
import com.example.socialmediaapp.layoutviews.profile.model.StrangerProfileConfigurer;
import com.example.socialmediaapp.viewmodel.ViewProfileViewModel;
import com.example.socialmediaapp.viewmodel.models.user.profile.SelfProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.NotMeProfile;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileFragment extends Fragment implements FragmentAnimation {

    public class ProfileViewExtra extends RecyclerViewExtra {
        private MutableLiveData<ProfileView> profileViewLiveData;

        public ProfileViewExtra() {
            super(new FrameLayout(getContext()), Position.START);
            profileViewLiveData = new MutableLiveData<>();
        }

        @Override
        public void configure(View view) {
            profileViewLiveData.observe(getViewLifecycleOwner(), profileView -> {
                profileView.initViewModel();
                ViewGroup container = (ViewGroup) view;
                container.addView(profileView);
            });
        }
    }

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

    private Integer viewProfilesessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        viewProfilesessionId = args.getInt("session id");
    }

    private CircleButton avatarButton;
    private ViewGroup contentPanel;
    private TextView fullnameTextView;
    private ViewProfileViewModel viewModel;
    private View infoView;
    private ViewGroup root;
    private ProfileViewExtra profileViewExtra;

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

    private void initViewModel(ViewProfileSessionHandler sessionHandler) {
        viewModel = new ViewProfileViewModel(sessionHandler);
        LiveData<UserProfile> userProfile = viewModel.getLiveData();
        userProfile.observe(getViewLifecycleOwner(), userProfile1 -> {
            ProfileView profileView = onLoadedUserInformation(viewModel.getViewProfileSessionHandler(), userProfile1);
            profileViewExtra.profileViewLiveData.setValue(profileView);
        });
    }

    private void initPostFragment(LiveData<SessionHandler> postDataAccessHandler) {

        List<RecyclerViewExtra> extras = new ArrayList<>();
        extras.add(profileViewExtra = new ProfileViewExtra());

        List<PostFragment.ConfigureExtra> configureExtras = new ArrayList<>();
        configureExtras.add(new PostFragment.ScrollConfigurator());

        PostFragment postFragment = new PostFragment(postDataAccessHandler, extras, configureExtras);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.post_fragment_container, postFragment);
        fragmentTransaction.commit();
    }

    private void startLoadProfile() {
        SessionHandler.SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
        LiveData<SessionHandler> sessionHandlerLiveData = sessionRepository.getSessionById(viewProfilesessionId);
        LiveData<SessionHandler> postDataAccessHandler = Transformations.map(sessionHandlerLiveData, new Function<SessionHandler, SessionHandler>() {
            @Override
            public SessionHandler apply(SessionHandler input) {
                return ((ViewProfileSessionHandler) input).getPostRepositorySession();
            }
        });
        initPostFragment(postDataAccessHandler);
        sessionHandlerLiveData.observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                initViewModel((ViewProfileSessionHandler) sessionHandler);
            }
        });
    }

    private void initValues() {
        if (userBasicInfo == null) return;
        Bitmap avt = userBasicInfo.getAvatar();
        avatarButton.setBackgroundContent(avt == null ? null : new BitmapDrawable(getResources(), avt), 0);
        fullnameTextView.setText(userBasicInfo.getFullname());
    }

    private ProfileView onLoadedUserInformation(ViewProfileSessionHandler handler, UserProfile user) {
        contentPanel.removeView(infoView);
        if (user instanceof SelfProfile) {
            return new SelfProfileView(this);
        }
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
        return notMeProfileView;
    }

    @Override
    public void performEnd(Runnable endAction) {
        ApplicationContainer.getInstance().sessionRepository.deleteIfDetached(viewProfilesessionId);
        root.animate()
                .translationX(root.getWidth())
                .setDuration(200)
                .withEndAction(() -> endAction.run())
                .start();
    }

    @Override
    public void performStart() {
        root.setTranslationX(((View) root.getParent()).getWidth() * 66 / 100);
        root.animate()
                .translationX(0)
                .setDuration(200)
                .withEndAction(() -> startLoadProfile())
                .start();
    }
}