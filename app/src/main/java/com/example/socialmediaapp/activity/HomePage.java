package com.example.socialmediaapp.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.PostHandlerStore;
import com.example.socialmediaapp.application.session.SelfProfileSessionHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.session.UserProfileProvider;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.application.session.ProfileSessionHandler;
import com.example.socialmediaapp.home.fragment.FriendFragment;
import com.example.socialmediaapp.home.fragment.NotificationFragment;
import com.example.socialmediaapp.home.fragment.ViewImageFragment;
import com.example.socialmediaapp.home.fragment.post.PostDetailsFragment;
import com.example.socialmediaapp.application.repo.core.NotificationRepository;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.view.button.ActiveFragmentButton;
import com.example.socialmediaapp.home.fragment.main.LogoutInProgressFragment;
import com.example.socialmediaapp.home.fragment.comment.MainCommentFragment;
import com.example.socialmediaapp.home.fragment.post.CreatePostFragment;
import com.example.socialmediaapp.home.fragment.information.EditInformationFragment;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.information.SetUpInformationFragment;
import com.example.socialmediaapp.home.fragment.information.UpdateBackgroundFragment;
import com.example.socialmediaapp.home.fragment.post.MainPostFragment;
import com.example.socialmediaapp.home.fragment.information.UpdateAvatarFragment;
import com.example.socialmediaapp.home.fragment.post.ViewImagePostFragment;
import com.example.socialmediaapp.home.fragment.information.ProfileFragment;
import com.example.socialmediaapp.home.fragment.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.post.PostFragment;
import com.example.socialmediaapp.home.fragment.SettingsFragment;
import com.example.socialmediaapp.view.button.NotiButton;
import com.example.socialmediaapp.viewmodel.fragment.PostFragmentViewModel;
import com.example.socialmediaapp.models.UserSession;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.List;
import java.util.Objects;


public class HomePage extends AppCompatActivity {
  private HorizontalScrollView scroll_page;
  private ViewGroup page_panel;
  private UserSessionViewModel viewModel;
  private ActiveFragmentButton home_button, media_button, friends_button, settings_button;
  private NotiButton notify_button;
  private ActivityResultLauncher<String> pickAvatar;
  private ActivityResultLauncher<String> pickBackground;
  private LiveData<String> sessionState;
  private UserProfileProvider userProfileProvider = DecadeApplication.getInstance().onlineSessionHandler.getProfileProvider();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    initSession();

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home_page);

    pickAvatar = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
      @Override
      public void onActivityResult(Uri uri) {
        if (uri == null) return;
        openUpdateAvatarFragment(uri);
      }
    });
    pickBackground = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
      @Override
      public void onActivityResult(Uri uri) {
        if (uri == null) return;
        openUpdateBackgroundFragment(uri);
      }
    });


    scroll_page = findViewById(R.id.scroll_page);
    page_panel = findViewById(R.id.page_panel);
    home_button = findViewById(R.id.home_button);
    media_button = findViewById(R.id.media_button);
    notify_button = findViewById(R.id.notify_button);
    friends_button = findViewById(R.id.friends_button);
    settings_button = findViewById(R.id.settings_button);

    int maxWidth = getResources().getDisplayMetrics().widthPixels;

    findViewById(R.id.posts).getLayoutParams().width = maxWidth;
    findViewById(R.id.notifications).getLayoutParams().width = maxWidth;
    findViewById(R.id.media).getLayoutParams().width = maxWidth;
    findViewById(R.id.friends).getLayoutParams().width = maxWidth;
    findViewById(R.id.settings).getLayoutParams().width = maxWidth;

    initTouchBehaviour();
    initViewModel();

  }

  @Override
  protected void onStart() {
    viewModel.getHandler().setOnForeground(true);
    super.onStart();
  }

  @Override
  protected void onStop() {
    viewModel.getHandler().setOnForeground(false);
    super.onStop();
  }

  private void pushToMainBackStack(Integer i) {
    List<Integer> backStack = viewModel.getMainBackStack().getValue();
    if (!Objects.equals(i, backStack.get(backStack.size() - 1))) {
      if (backStack.size() == 3) {
        backStack.remove(1);
      }
      backStack.add(i);
    }
    viewModel.getMainBackStack().setValue(backStack);
  }

  private void popMainBackStack() {
    List<Integer> backStack = viewModel.getMainBackStack().getValue();
    int top = backStack.get(backStack.size() - 1);
    if (top == 0) {
      MainPostFragment postFragment = (MainPostFragment) getSupportFragmentManager().findFragmentByTag("post fragment");
      postFragment.performEnd(() -> finish());
      return;
    }
    backStack.remove(backStack.size() - 1);
    viewModel.getMainBackStack().setValue(backStack);
  }

  private void initPostFragment() {
    MainPostFragment postFragment = MainPostFragment.newInstance();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.posts, postFragment, "post fragment");
    fragmentTransaction.commit();
  }

  private void initFriendFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.friends, FriendFragment.newInstance(), "friend fragment");
    fragmentTransaction.commit();
  }

  private void initNotificationFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.notifications, NotificationFragment.newInstance(), "notification fragment");
    fragmentTransaction.commit();

    NotificationRepository repository = viewModel.getNotifyRepo();
    LiveData<Integer> countUnRead = repository.getCntUnRead();
    countUnRead.observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        notify_button.setCount(integer);
      }
    });

  }

  private void initSettingFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.settings, SettingsFragment.newInstance(), "setting fragment");
    fragmentTransaction.commit();
  }

  private void initSession() {
    OnlineSessionHandler onlineSessionHandler = OnlineSessionHandler.getInstance();
    UserSessionHandler userSessionHandler = onlineSessionHandler.getUserHandler();
    viewModel = new UserSessionViewModel(userSessionHandler);
  }

  private void initViewModel() {
    sessionState = viewModel.getSessionState();
    initPostFragment();
    initFriendFragment();
    initNotificationFragment();
    initSettingFragment();

    LiveData<UserSession> liveData = viewModel.getUserSession();
    liveData.observe(this, new Observer<UserSession>() {
      @Override
      public void onChanged(UserSession u) {
        boolean newBie = u.getUserInfo().getFullname() == null;
        if (newBie) {
          FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
          fragmentTransaction.add(R.id.fragment_container, SetUpInformationFragment.newInstance(), null);
          fragmentTransaction.commit();
        }
        liveData.removeObserver(this);
      }
    });
    LiveData<Integer> cur_fragment = viewModel.getCurFragment();
    cur_fragment.observe(this, new Observer<Integer>() {
      @Override
      public void onChanged(Integer s) {
        int offset = scroll_page.getWidth() * s;
        scroll_page.smoothScrollTo(offset, 0);
      }
    });
    cur_fragment.observe(this, s -> {
      home_button.setActive(s == 0);
      media_button.setActive(s == 1);
      friends_button.setActive(s == 2);
      notify_button.setActive(s == 3);
      settings_button.setActive(s == 4);
      NotificationFragment notificationFragment = (NotificationFragment)
              getSupportFragmentManager()
                      .findFragmentByTag("notification fragment");

      if (s == 3) {
        notificationFragment.switchTo();
      } else {
        notificationFragment.switchOff();
      }
      if (s == 2) {
        FriendFragment friendFragment = (FriendFragment) getSupportFragmentManager().findFragmentByTag("friend fragment");
        friendFragment.switchTo();
      }
    });


  }

  private void initTouchBehaviour() {
    home_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pushToMainBackStack(0);
      }
    });
    media_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pushToMainBackStack(1);
      }
    });
    friends_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pushToMainBackStack(2);
      }
    });
    notify_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pushToMainBackStack(3);
      }
    });
    settings_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pushToMainBackStack(4);
      }
    });
    scroll_page.setOnTouchListener(new View.OnTouchListener() {
      float pre0, pre1;

      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        float xx = motionEvent.getX();
        switch (motionEvent.getAction()) {
          case MotionEvent.ACTION_UP:
            float w = scroll_page.getWidth();
            float x = scroll_page.getScrollX();
            int block = (int) (x / w);
            x -= block * w;
            if (pre0 > pre1) {
              if (pre0 - pre1 >= 5 || x > w / 2) {
                pushToMainBackStack(block + 1);
              } else {
                pushToMainBackStack(block);
              }
            } else {
              if (pre1 - pre0 >= 5 || x <= w / 2) {
                pushToMainBackStack(block);
              } else {
                pushToMainBackStack(block + 1);
              }
            }
            pre1 = -1;
            return true;
          default:
            break;
        }
        pre0 = pre1;
        pre1 = xx;
        return false;
      }
    });

  }

  public UserSessionViewModel getViewModel() {
    return viewModel;
  }

  private boolean waitForPopping = false;

  @Override
  public void onBackPressed() {
    if (findTopFragment() instanceof LogoutInProgressFragment) {
      return;
    }
    if (waitForPopping) return;
    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
      popMainBackStack();
      return;
    }
    Fragment top = findTopFragment();
    if (top instanceof FragmentAnimation) {
      waitForPopping = true;
      FragmentAnimation animation = (FragmentAnimation) top;
      animation.performEnd(new Runnable() {
        @Override
        public void run() {
          waitForPopping = false;
          getSupportFragmentManager().popBackStackImmediate(top.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
      });
      return;
    }
    super.onBackPressed();
  }

  private Fragment findTopFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    int backStackLen = fragmentManager.getBackStackEntryCount();
    if (backStackLen > 0) {
      String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
      return fragmentManager.findFragmentByTag(tag);
    }
    return null;
  }

  public void openUpdateAvatarFragment() {
    pickAvatar.launch("image/*");
  }

  public void openUpdateBackgroundFragment() {
    pickBackground.launch("image/*");
  }

  private void openUpdateBackgroundFragment(Uri image) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "update background";
    fragmentTransaction.add(R.id.fragment_container, UpdateBackgroundFragment.newInstance(image), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openEditInformationFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "edit information";
    fragmentTransaction.add(R.id.fragment_container, EditInformationFragment.newInstance(), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  private void openUpdateAvatarFragment(Uri image) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "update avatar";
    fragmentTransaction.add(R.id.fragment_container, UpdateAvatarFragment.newInstance(image), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openViewProfileFragment(UserBasicInfoModel author) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "view profile " + author.getId();
    fragmentTransaction.add(R.id.fragment_container
            , ProfileFragment.newInstance(author)
            , tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openCreatePostFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "create post";
    fragmentTransaction.add(R.id.fragment_container, CreatePostFragment.newInstance(), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openCommentFragment(HandlerAccess handlerAccess) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "comment";
    fragmentTransaction.add(R.id.fragment_container, MainCommentFragment.newInstance(handlerAccess), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openSearchFragment() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "search";
    fragmentTransaction.add(R.id.fragment_container, SearchFragment.newInstance(), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public MutableLiveData<String> updateAvatar(Bundle data) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    LiveData<SelfProfileSessionHandler> viewProfileSession = userProfileProvider.getSelfProfile();

    viewProfileSession.observe(this, new Observer<SelfProfileSessionHandler>() {
      @Override
      public void onChanged(SelfProfileSessionHandler selfProfileSessionHandler) {
        viewProfileSession.removeObserver(this);
        selfProfileSessionHandler.changeAvatar(data).observe(HomePage.this, new Observer<HandlerAccess>() {
          @Override
          public void onChanged(HandlerAccess handlerAccess) {
            callBack.postValue(handlerAccess == null ? "Failed" : "Success");
            if (handlerAccess == null) {
              return;
            }
            MainPostFragment mainPostFragment = (MainPostFragment)
                    getSupportFragmentManager()
                            .findFragmentByTag("post fragment");
            PostFragment postFragment = (PostFragment) mainPostFragment
                    .getChildFragmentManager()
                    .findFragmentByTag("posts");
            PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
            Repository<HandlerAccess> postRepo = postFragmentViewModel.getPostRepo();
            postRepo.updateNewItems(handlerAccess);
          }
        });
      }
    });
    return callBack;
  }

  public MutableLiveData<String> updateBackground(Bundle data) {

    MutableLiveData<String> callBack = new MutableLiveData<>();
    LiveData<SelfProfileSessionHandler> viewProfile = userProfileProvider.getSelfProfile();

    viewProfile.observe(this, new Observer<SelfProfileSessionHandler>() {
      @Override
      public void onChanged(SelfProfileSessionHandler selfProfileSessionHandler) {
        viewProfile.removeObserver(this);
        selfProfileSessionHandler.changeBackground(data).observe(HomePage.this, new Observer<HandlerAccess>() {
          @Override
          public void onChanged(HandlerAccess handlerAccess) {
            callBack.postValue(handlerAccess == null ? "Failed" : "Success");
            if (handlerAccess == null) {
              return;
            }
            MainPostFragment mainPostFragment = (MainPostFragment)
                    getSupportFragmentManager()
                            .findFragmentByTag("post fragment");
            PostFragment postFragment = (PostFragment) mainPostFragment
                    .getChildFragmentManager()
                    .findFragmentByTag("posts");
            PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
            Repository<HandlerAccess> postRepo = postFragmentViewModel.getPostRepo();
            postRepo.updateNewItems(handlerAccess);
          }
        });
      }
    });
    return callBack;
  }

  public void finishFragment(String tag) {
    Fragment top = findTopFragment();
    if (top != null && top.getTag().startsWith(tag)) {
      onBackPressed();
    }
  }

  public void openPostDetailsFragment(String postId, Integer accessId, String commentId) {
    LiveData<HandlerAccess> sessionAccessLiveData = PostHandlerStore
            .getInstance()
            .findHandlerAccess(postId, accessId);
    sessionAccessLiveData.observe(this, sessionAccess -> {
      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      String tag = "view post details";
      fragmentTransaction.add(R.id.fragment_container
              , PostDetailsFragment.newInstance(sessionAccess, commentId)
              , tag);
      fragmentTransaction.addToBackStack(tag);
      fragmentTransaction.commit();
    });

  }

  public void openViewImageFragment(String imageUri) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
            .beginTransaction();
    String tag = "view image " + imageUri;
    fragmentTransaction.replace(R.id.view_image_fragment, ViewImageFragment.newInstance(imageUri), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openViewImagePostFragment(HandlerAccess handlerAccess, Rect bound) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    String tag = "view image";
    fragmentTransaction.add(R.id.fragment_container
            , ViewImagePostFragment.newInstance(handlerAccess, bound)
            , tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void doLogout() {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(R.id.fragment_container
            , LogoutInProgressFragment.newInstance()
            , "logout");
    fragmentTransaction.addToBackStack("logout");
    fragmentTransaction.commit();
    OnlineSessionHandler onlineSessionHandler = DecadeApplication.getInstance().onlineSessionHandler;
    LiveData<String> callBack = onlineSessionHandler.logout();
    callBack.observe(this, s -> {
      Intent intent = new Intent(HomePage.this, LoginFormActivity.class);
      startActivity(intent);
      Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
      finish();
    });
  }
}