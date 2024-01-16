package com.example.socialmediaapp.home.fragment.post;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.models.UserSession;
import com.example.socialmediaapp.models.user.UserInformation;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.container.SpinningFrame;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.fragment.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.util.ArrayList;
import java.util.List;

public class MainPostFragment extends Fragment implements FragmentAnimation {

  public class CreatePostPanelExtra extends RecyclerViewExtra {
    private CircleButton avatarButton;
    private EditText create_post_edit_text;

    public CreatePostPanelExtra(View createPostPanel) {
      super(createPostPanel, Position.START);
    }

    @Override
    public void configure(View view) {
      avatarButton = view.findViewById(R.id.avatar_button);
      create_post_edit_text = view.findViewById(R.id.open_create_post);
      initAvatarButton();
      initCreatePostEditText();
    }

    private void initAvatarButton() {
      UserSessionViewModel hostViewModel = ((HomePage) getActivity()).getViewModel();
      LiveData<Bitmap> avatar = hostViewModel.getAvatar();
      LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
      LiveData<UserSession> liveData = hostViewModel.getUserSession();
      liveData.observe(getViewLifecycleOwner(), new Observer<UserSession>() {
        @Override
        public void onChanged(UserSession userSession) {
          UserBasicInfoModel userModel = new UserBasicInfoModel();
          UserInformation info = userSession.getUserInfo();
          userModel.setFullname(info.getFullname());
          userModel.setId(info.getId());
          userModel.setAlias(info.getAlias());
          userModel.setAvatarUri(userSession.getAvatarUri());
          homePage.openViewProfileFragment(userModel);
        }
      });
      avatar.observe(lifecycleOwner, bitmap -> avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), bitmap), 0));
    }

    private void initCreatePostEditText() {
      create_post_edit_text.setKeyListener(null);
      create_post_edit_text.setOnTouchListener((view, event) -> {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            create_post_edit_text.animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).start();
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            create_post_edit_text.animate().scaleX(1).scaleY(1).setDuration(50).start();
            if (event.getAction() == MotionEvent.ACTION_UP) {
              HomePage homePage = (HomePage) getActivity();
              homePage.openCreatePostFragment();
            }
            break;
          default:
            break;
        }
        return true;
      });
    }
  }

  public class SpinFrameConfigurator implements PostFragment.ConfigureExtra {
    private RecyclerView recyclerView;

    @Override
    public void apply(View root, PostFragmentViewModel fragmentViewModel) {
      recyclerView = root.findViewById(R.id.posts_panel);
      initSpinFrameAction(fragmentViewModel);
    }

    public void initSpinFrameAction(PostFragmentViewModel fragmentViewModel) {
      spinningFrame.setHelper(new SpinningFrame.SpinHelper() {
        @Override
        public LiveData<?> doAction() {
          return fragmentViewModel.getPostRepo().renew();
        }

        @Override
        public boolean isAtTop() {
          LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
          return layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
        }
      });
    }
  }

  public class BackPressRefreshConfigurator implements PostFragment.ConfigureExtra {
    private RecyclerView recyclerView;

    @Override
    public void apply(View root, PostFragmentViewModel fragmentViewModel) {
      recyclerView = root.findViewById(R.id.posts_panel);
    }

    public boolean onBackPressed() {
      LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
      if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {

        int pos = layoutManager.findFirstCompletelyVisibleItemPosition();
        if (pos >= 5) {
          recyclerView.scrollToPosition(5);
        }

        recyclerView.smoothScrollToPosition(0);
        spinningFrame.doLoad();
        return false;
      }
      return true;
    }
  }


  public MainPostFragment() {
  }

  public static MainPostFragment newInstance() {
    MainPostFragment fragment = new MainPostFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private CircleButton searchButton;
  private HomePage homePage;
  private SpinningFrame spinningFrame;
  private CircleButton messageButton;
  private BackPressRefreshConfigurator backPressRefreshConfigurator;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_main_posts, container, false);
    spinningFrame = view.findViewById(R.id.spinning_frame);
    searchButton = view.findViewById(R.id.search_button);
    messageButton = view.findViewById(R.id.message_button);

    homePage = (HomePage) getActivity();


    UserSessionViewModel hostViewModel = homePage.getViewModel();
    initOnclick();
    createPostFragment(hostViewModel.getPostRepo());
    return view;
  }

  private void createPostFragment(Repository<HandlerAccess> repo) {
    ViewGroup createPostPanel = new FrameLayout(getContext());
    LayoutInflater.from(getContext()).inflate(R.layout.create_post_layout, createPostPanel, true);

    List<RecyclerViewExtra> extras = new ArrayList<>();
    extras.add(new CreatePostPanelExtra(createPostPanel));

    List<PostFragment.ConfigureExtra> configureExtras = new ArrayList<>();
    backPressRefreshConfigurator = new BackPressRefreshConfigurator();
    configureExtras.add(new PostFragment.ScrollConfigurator());
    configureExtras.add(new SpinFrameConfigurator());
    configureExtras.add(backPressRefreshConfigurator);

    PostFragment postFragment = new PostFragment(repo, extras, configureExtras);
    FragmentTransaction fTran = getChildFragmentManager().beginTransaction();
    fTran.replace(R.id.post_fragment_container, postFragment, "posts");
    fTran.commit();
  }

  private void initOnclick() {
    searchButton.setOnClickListener(view -> homePage.openSearchFragment());
    messageButton.setOnClickListener(view -> {
      Intent intent = new Intent(getContext(), MessageHome.class);
      startActivity(intent);
    });
  }

  public void onDestroyView() {
    super.onDestroyView();
    spinningFrame.endSpin();
  }

  @Override
  public void performEnd(Runnable endAction) {
    if (backPressRefreshConfigurator.onBackPressed()) {
      endAction.run();
    }
  }

  @Override
  public void performStart() {
  }

}
