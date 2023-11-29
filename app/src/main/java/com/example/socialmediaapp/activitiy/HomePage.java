package com.example.socialmediaapp.activitiy;

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

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.customview.button.ActiveFragmentButton;
import com.example.socialmediaapp.home.fragment.MainCommentFragment;
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
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.PostDataViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;


public class HomePage extends AppCompatActivity {

    private HorizontalScrollView scroll_page;
    private ViewGroup page_panel;
    private UserSessionViewModel viewModel;
    private ActiveFragmentButton home_button, media_button, friends_button, notify_button, settings_button;
    private ActivityResultLauncher<String> pickAvatar;
    private ActivityResultLauncher<String> pickBackground;
    private ViewGroup root;
    private MutableLiveData<String> sessionState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        root = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

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


        initViewModel();
        initTouchBehaviour();
    }

    private void initPostConstruct() {
        MainPostFragment postFragment = MainPostFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.posts, postFragment, "post fragment");
        fragmentTransaction.commit();

    }

    private void initViewModel() {
        SessionHandler.SessionRegistry rootSession = ApplicationContainer.getInstance().onlineSessionHandler.getSessionRegistry();
        UserSessionHandler userSessionHandler = new UserSessionHandler();

        rootSession.bindSession(userSessionHandler);

        viewModel = new UserSessionViewModel(userSessionHandler);
        sessionState = userSessionHandler.getSessionState();
        sessionState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("started")) {
                    initPostConstruct();
                    sessionState.removeObserver(this);
                }
            }
        });

        LiveData<UserSession> liveData = viewModel.getLiveData();
        liveData.observe(this, new Observer<UserSession>() {
            @Override
            public void onChanged(UserSession userSession) {
                boolean newBie = userSession.getUserInfo() == null;
                if (newBie) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    FrameLayout container = new FrameLayout(HomePage.this);
                    container.setId(View.generateViewId());
                    root.addView(container);
                    fragmentTransaction.add(container.getId(), SetUpInformationFragment.newInstance(), null);
                    fragmentTransaction.commit();
                }
                liveData.removeObserver(this);
            }
        });
        MutableLiveData<Integer> cur_fragment = viewModel.getCurFragment();
        cur_fragment.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                int offset = scroll_page.getWidth() * s;
                ObjectAnimator.ofInt(scroll_page, "scrollX", offset)
                        .setDuration(200)
                        .start();
            }
        });
        cur_fragment.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                home_button.setActive(s == 0);
                media_button.setActive(s == 1);
                friends_button.setActive(s == 2);
                notify_button.setActive(s == 3);
                settings_button.setActive(s == 4);
            }
        });
        cur_fragment.setValue(0);
    }

    private void initTouchBehaviour() {
        MutableLiveData<Integer> cur_fragment = viewModel.getCurFragment();
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_fragment.setValue(0);
            }
        });
        media_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_fragment.setValue(1);
            }
        });
        friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_fragment.setValue(2);
            }
        });
        notify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_fragment.setValue(3);
            }
        });
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_fragment.setValue(4);
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
                                cur_fragment.setValue(block + 1);
                            } else {
                                cur_fragment.setValue(block);
                            }
                        } else {
                            if (pre1 - pre0 >= 5 || x <= w / 2) {
                                cur_fragment.setValue(block);
                            } else {
                                cur_fragment.setValue(block + 1);
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
        Fragment top = findTopFragment();
        if (top instanceof FragmentAnimation) {
            waitForPopping = true;
            FragmentAnimation animation = (FragmentAnimation) top;
            animation.performEnd(new Runnable() {
                @Override
                public void run() {
                    View parent = (View) top.getView().getParent();
                    waitForPopping = false;
                    getSupportFragmentManager().popBackStackImmediate(top.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    root.removeView(parent);
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
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "update background" + Integer.toString(container.getId());
        fragmentTransaction.add(container.getId(), UpdateBackgroundFragment.newInstance(image), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openEditInformationFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "edit information" + Integer.toString(container.getId());
        fragmentTransaction.add(container.getId(), EditInformationFragment.newInstance(), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private void openUpdateAvatarFragment(Uri image) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "update avatar" + Integer.toString(container.getId());
        fragmentTransaction.add(container.getId(), UpdateAvatarFragment.newInstance(image), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openViewProfileFragment(UserBasicInfo author) {
        LiveData<Integer> sessionId = viewModel.createViewProfileSessionId(author.getAlias());
        sessionId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Bundle args = new Bundle();
                args.putInt("session id", integer);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                FrameLayout container = new FrameLayout(HomePage.this);
                container.setId(View.generateViewId());
                root.addView(container);
                String tag = "view profile" + Integer.toString(container.getId());
                fragmentTransaction.add(container.getId(), ViewProfileFragment.newInstance(args, author), tag);
                fragmentTransaction.addToBackStack(tag);
                fragmentTransaction.commit();
                sessionId.removeObserver(this);
            }
        });
    }

    public void openCreatePostFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "create post";
        fragmentTransaction.replace(container.getId(), CreatePostFragment.newInstance(), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openCommentFragment(Bundle args, PostDataViewModel postDataViewModel) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        String tag = "comment";
        fragmentTransaction.replace(R.id.comment_fragment, MainCommentFragment.newInstance(args, postDataViewModel), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void openSearchFragment() {
        LiveData<Integer> recentSearchId = viewModel.getSearchSessionId();
        recentSearchId.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                FrameLayout container = new FrameLayout(HomePage.this);
                container.setId(View.generateViewId());
                root.addView(container);
                String tag = "search";
                Bundle args = new Bundle();
                args.putInt("session id", integer);
                fragmentTransaction.replace(container.getId(), SearchFragment.newInstance(args), tag);
                fragmentTransaction.addToBackStack(tag);
                fragmentTransaction.commit();

                recentSearchId.removeObserver(this);
            }
        });

    }

    public void finishFragment(String tag) {
        Fragment top = findTopFragment();
        if (top != null && top.getTag().startsWith(tag)) {
            onBackPressed();
        }
    }

    public void openViewImageFragment(Bundle args, Rect bound, Bitmap image) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "view image" + Integer.toString(container.getId());
        fragmentTransaction.add(container.getId(), ViewImageFragment.newInstance(args, bound, image), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public View getCommandPanel() {
        return findViewById(R.id.command_panel);
    }

    public View getCommandFrame() {
        return findViewById(R.id.command_frame);
    }
}