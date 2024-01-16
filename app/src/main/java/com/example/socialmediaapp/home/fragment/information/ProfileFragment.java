package com.example.socialmediaapp.home.fragment.information;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.session.ProfileSessionHandler;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.post.PostFragment;
import com.example.socialmediaapp.layoutviews.profile.SelfProfileView;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.layoutviews.profile.base.ProfileView;
import com.example.socialmediaapp.layoutviews.profile.model.Configuror;
import com.example.socialmediaapp.layoutviews.profile.model.FriendProfileConfiguror;
import com.example.socialmediaapp.layoutviews.profile.model.FriendRequestProfileConfiguror;
import com.example.socialmediaapp.layoutviews.profile.model.RequestFriendProfileConfiguror;
import com.example.socialmediaapp.layoutviews.profile.model.StrangerProfileConfiguror;
import com.example.socialmediaapp.viewmodel.ProfileViewModel;
import com.example.socialmediaapp.models.user.profile.SelfProfileModel;
import com.example.socialmediaapp.models.user.profile.NotMeProfileModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.models.user.profile.base.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements FragmentAnimation {

  public class ProfileViewExtra extends RecyclerViewExtra {
    private MutableLiveData<ProfileModel> profileModel;

    public ProfileViewExtra() {
      super(new FrameLayout(getContext()), Position.START);
      profileModel = new MutableLiveData<>();
    }

    private void setView(ProfileView profileView) {
      ViewGroup container = (ViewGroup) view;
      container.addView(profileView);
      profileView.initViewModel();
    }

    @Override
    public void configure(View view) {
      profileModel.observe(getViewLifecycleOwner(), new Observer<ProfileModel>() {
        @Override
        public void onChanged(ProfileModel model) {
          if (model instanceof SelfProfileModel) {
            setView(new SelfProfileView(ProfileFragment.this));
            return;
          }
          ViewGroup p = (ViewGroup) view;
          boolean isNew = p.getChildCount() == 0;
          NotMeProfileModel notMeProfile = (NotMeProfileModel) model;

          NotMeProfileView profileView = isNew
                  ? new NotMeProfileView(ProfileFragment.this)
                  : (NotMeProfileView) p.getChildAt(0);
          if (isNew) {
            setView(profileView);
          }
          Configuror configuror = null;

          switch (notMeProfile.getType()) {
            case "stranger":
              configuror = new StrangerProfileConfiguror(profileView);
              break;
            case "friend request":
              configuror = new FriendRequestProfileConfiguror(profileView);
              break;
            case "request friend":
              configuror = new RequestFriendProfileConfiguror(profileView);
              break;
            case "friend":
              configuror = new FriendProfileConfiguror(profileView);
              break;
          }
          profileView.setConfiguration(configuror);
        }
      });
    }
  }

  private UserBasicInfoModel userModel;

  public ProfileFragment() {
  }

  public ProfileFragment(UserBasicInfoModel userModel) {
    this.userModel = userModel;
  }

  public static ProfileFragment newInstance(UserBasicInfoModel userModel) {
    ProfileFragment fragment = new ProfileFragment(userModel);
    Bundle args = new Bundle();
    args.putString("userId", userModel.getId());
    fragment.setArguments(args);
    return fragment;
  }

  private String userId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    userId = args.getString("userId");
  }

  private CircleButton avatarButton;
  private ViewGroup contentPanel;
  private TextView fullnameTextView;
  private ProfileViewModel viewModel;
  private View infoView;
  private ViewGroup root;
  private ProfileViewExtra profileViewExtra;

  public ProfileViewModel getViewModel() {
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
    initValues();

    root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        performStart();
        root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
    return root;
  }

  private void initViewModel(ProfileSessionHandler handler) {
    viewModel = new ProfileViewModel(handler);
    LiveData<ProfileModel> profileModel = viewModel.getLiveData();
    profileModel.observe(getViewLifecycleOwner(), new Observer<ProfileModel>() {
      @Override
      public void onChanged(ProfileModel model) {
        contentPanel.removeView(infoView);
        profileViewExtra.profileModel.setValue(model);
      }
    });
  }

  private void initPostFragment(DataAccessHandler<HandlerAccess> postAccess) {

    List<RecyclerViewExtra> extras = new ArrayList<>();
    extras.add(profileViewExtra = new ProfileViewExtra());

    List<PostFragment.ConfigureExtra> configs = new ArrayList<>();
    configs.add(new PostFragment.ScrollConfigurator());

    PostFragment postFragment = new PostFragment(new Repository<>(postAccess), extras, configs);
    FragmentTransaction fTran = getChildFragmentManager().beginTransaction();
    fTran.add(R.id.post_fragment_container, postFragment);
    fTran.commit();
  }

  private void startLoadProfile() {
    LiveData<ProfileSessionHandler> profileHandler = DecadeApplication
            .getInstance()
            .onlineSessionHandler
            .getProfileProvider()
            .getUserProfile(userId);
    profileHandler.observe(getViewLifecycleOwner(), new Observer<ProfileSessionHandler>() {
      @Override
      public void onChanged(ProfileSessionHandler handler) {
        initPostFragment(handler.getPostDataAccess());
        initViewModel(handler);
      }
    });
  }

  private void initValues() {
    if (userModel == null) return;
    LiveData<Bitmap> avt = ImageUtils.load(userModel.getAvatarUri());
    avt.observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
      @Override
      public void onChanged(Bitmap bitmap) {
        avatarButton.setBackgroundContent(bitmap == null ? null : new BitmapDrawable(getResources(), bitmap), 0);
        fullnameTextView.setText(userModel.getFullname());
      }
    });
  }

  @Override
  public void performEnd(Runnable endAction) {
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