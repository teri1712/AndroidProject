package com.example.socialmediaapp.activitiy;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.SelfProfileSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.customview.button.ActiveFragmentButton;
import com.example.socialmediaapp.home.fragment.main.MainCommentFragment;
import com.example.socialmediaapp.home.fragment.CreatePostFragment;
import com.example.socialmediaapp.home.fragment.EditInformationFragment;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.SetUpInformationFragment;
import com.example.socialmediaapp.home.fragment.UpdateBackgroundFragment;
import com.example.socialmediaapp.home.fragment.main.MainPostFragment;
import com.example.socialmediaapp.home.fragment.UpdateAvatarFragment;
import com.example.socialmediaapp.home.fragment.ViewImageFragment;
import com.example.socialmediaapp.home.fragment.ViewProfileFragment;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.HashMap;
import java.util.List;


public class HomePage extends AppCompatActivity {
    private HorizontalScrollView scroll_page;
    private ViewGroup page_panel;
    private UserSessionViewModel viewModel;
    private ActiveFragmentButton home_button, media_button, friends_button, notify_button, settings_button;
    private ActivityResultLauncher<String> pickAvatar;
    private ActivityResultLauncher<String> pickBackground;
    private LiveData<String> sessionState;
    private OnlineSessionHandler.UserProfileProvider userProfileProvider = ApplicationContainer.getInstance().onlineSessionHandler.getUserProfileProvider();

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

        scroll_page.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                findViewById(R.id.posts).getLayoutParams().width = scroll_page.getWidth();
                findViewById(R.id.notifications).getLayoutParams().width = scroll_page.getWidth();
                findViewById(R.id.media).getLayoutParams().width = scroll_page.getWidth();
                findViewById(R.id.friends).getLayoutParams().width = scroll_page.getWidth();
                findViewById(R.id.settings).getLayoutParams().width = scroll_page.getWidth();

                page_panel.requestLayout();
                scroll_page.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        initTouchBehaviour();
        initViewModel();

    }

    private void pushToMainBackStack(Integer i) {
        List<Integer> backStack = viewModel.getMainBackStack().getValue();
        if (i == backStack.get(backStack.size() - 1)) return;
        if (backStack.size() == 3) {
            backStack.remove(1);
        }
        backStack.add(i);
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

    private void initSession() {
        Integer sessionId = getIntent().getIntExtra("session id", -1);
        SessionHandler.SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
        LiveData<SessionHandler> sessionHandlerLiveData = sessionRepository.getSessionById(sessionId);
        viewModel = new UserSessionViewModel(sessionHandlerLiveData);
    }

    private void initViewModel() {
        sessionState = viewModel.getSessionState();
        initPostFragment();

        LiveData<UserSession> liveData = viewModel.getLiveData();
        liveData.observe(this, new Observer<UserSession>() {
            @Override
            public void onChanged(UserSession userSession) {
                boolean newBie = userSession.getUserInfo().getFullname() == null;
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
                ObjectAnimator.ofInt(scroll_page, "scrollX", offset)
                        .setDuration(100)
                        .start();
            }
        });
        cur_fragment.observe(this, s -> {
            home_button.setActive(s == 0);
            media_button.setActive(s == 1);
            friends_button.setActive(s == 2);
            notify_button.setActive(s == 3);
            settings_button.setActive(s == 4);
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
                        if (x == 0) return false;
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
                        pre0 = pre1;
                        pre1 = xx;
                        break;
                }
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

    public void openViewProfileFragment(UserBasicInfo author) {
        LiveData<Integer> sid = userProfileProvider.getViewUserProfileSessionId(author.getAlias());

        sid.observe(this, integer -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putInt("session id", integer);
            String tag = "view profile " + args.getInt("session id");
            fragmentTransaction.add(R.id.fragment_container, ViewProfileFragment.newInstance(args, author), tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        });
    }

    public void openCreatePostFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        String tag = "create post";
        fragmentTransaction.add(R.id.fragment_container, CreatePostFragment.newInstance(), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openCommentFragment(Bundle args, LiveData<Integer> countLikeHost) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        String tag = "comment";
        fragmentTransaction.add(R.id.fragment_container, MainCommentFragment.newInstance(args, countLikeHost), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openSearchFragment() {
        LiveData<Integer> searchSessionId = viewModel.getSearchSessionId();
        searchSessionId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                String tag = "search";
                Bundle args = new Bundle();
                args.putInt("session id", integer);
                fragmentTransaction.add(R.id.fragment_container, SearchFragment.newInstance(args), tag);
                fragmentTransaction.addToBackStack(tag);
                fragmentTransaction.commit();

                searchSessionId.removeObserver(this);
            }
        });

    }

    public MutableLiveData<String> updateAvatar(Bundle data) {
        LiveData<SelfProfileSessionHandler> viewProfileSession = userProfileProvider.getSelfProfile();

        MutableLiveData<String> callBack = new MutableLiveData<>();
        MainPostFragment mainPostFragment = (MainPostFragment) (getSupportFragmentManager().findFragmentByTag("post fragment"));
        PostFragment postFragment = (PostFragment) mainPostFragment.getChildFragmentManager().findFragmentByTag("posts");
        PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
        postFragmentViewModel.uploadPost(data).observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                String status = (String) hashMap.get("status");
                ImagePost item = (ImagePost) hashMap.get("item");
                callBack.setValue(status);

                if (!status.equals("Success")) return;

                viewProfileSession.observe(HomePage.this, sp -> sp.emitNewAvatarPost(item));

            }
        });
        return callBack;
    }

    public MutableLiveData<String> updateBackground(Bundle data) {

        LiveData<SelfProfileSessionHandler> viewProfileSession = userProfileProvider.getSelfProfile();

        MutableLiveData<String> callBack = new MutableLiveData<>();
        MainPostFragment mainPostFragment = (MainPostFragment) getSupportFragmentManager().findFragmentByTag("post fragment");
        PostFragment postFragment = (PostFragment) mainPostFragment.getChildFragmentManager().findFragmentByTag("posts");
        PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
        postFragmentViewModel.uploadPost(data).observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                String status = (String) hashMap.get("status");
                ImagePost item = (ImagePost) hashMap.get("item");
                callBack.setValue(status);

                if (!status.equals("Success")) return;
                viewProfileSession.observe(HomePage.this, sp -> sp.emitNewBackgroundPost(item));
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

    public void openViewImageFragment(Bundle args, Rect bound, Bitmap image) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        String tag = "view image";
        fragmentTransaction.add(R.id.fragment_container, ViewImageFragment.newInstance(args, bound, image), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

}