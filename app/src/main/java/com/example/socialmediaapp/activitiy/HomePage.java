package com.example.socialmediaapp.activitiy;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ObjectAnimator;
import android.content.Intent;
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
import com.example.socialmediaapp.customview.button.ActiveFragmentButton;
import com.example.socialmediaapp.home.fragment.CommentFragment;
import com.example.socialmediaapp.home.fragment.CreatePostFragment;
import com.example.socialmediaapp.home.fragment.EditInformationFragment;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.SetUpInformationFragment;
import com.example.socialmediaapp.home.fragment.UpdateBackgroundFragment;
import com.example.socialmediaapp.home.fragment.main.FriendFragment;
import com.example.socialmediaapp.home.fragment.main.NotificationFragment;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.home.fragment.main.SettingsFragment;
import com.example.socialmediaapp.home.fragment.UpdateAvatarFragment;
import com.example.socialmediaapp.home.fragment.main.VideoFragment;
import com.example.socialmediaapp.home.fragment.ViewImageFragment;
import com.example.socialmediaapp.home.fragment.ViewProfileFragment;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodels.HomePageViewModel;
import com.example.socialmediaapp.viewmodels.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodels.items.PostItemViewModel;
import com.example.socialmediaapp.viewmodels.models.HomePageContent;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.repo.Update;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;

import java.util.HashMap;
import java.util.Map;


public class HomePage extends AppCompatActivity {

    private HorizontalScrollView scroll_page;
    private ViewGroup page_panel;
    private HashMap<String, Integer> fragments;
    private HomePageViewModel viewModel;
    private ActiveFragmentButton home_button, media_button, friends_button, notify_button, settings_button;

    private ActivityResultLauncher<String> pickAvatar;
    private ActivityResultLauncher<String> pickBackground;
    private ViewGroup root;
    private boolean newBie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        root = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        Intent intent = getIntent();
        newBie = intent.getBooleanExtra("new bie", false);

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


        scroll_page = (HorizontalScrollView) findViewById(R.id.scroll_page);
        page_panel = (ViewGroup) findViewById(R.id.page_panel);
        home_button = (ActiveFragmentButton) findViewById(R.id.home_button);
        media_button = (ActiveFragmentButton) findViewById(R.id.media_button);
        notify_button = (ActiveFragmentButton) findViewById(R.id.notify_button);
        friends_button = (ActiveFragmentButton) findViewById(R.id.friends_button);
        settings_button = (ActiveFragmentButton) findViewById(R.id.settings_button);

        initViewModel();
        initTouchBehaviour();
        initViewPostConstruct();
    }

    private void initViewPostConstruct() {
        fragments = new HashMap<>();
        fragments.put("posts", R.id.posts);
        fragments.put("media", R.id.media);
        fragments.put("friends", R.id.friends);
        fragments.put("notifications", R.id.notifications);
        fragments.put("settings", R.id.settings);
        scroll_page.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                for (Map.Entry<String, Integer> i : fragments.entrySet()) {
                    findViewById(i.getValue()).getLayoutParams().width = scroll_page.getWidth();
                    Fragment myFragment;
                    switch (i.getKey()) {
                        case "posts":
                            myFragment = PostFragment.newInstance();
                            break;
                        case "notifications":
                            myFragment = new NotificationFragment();
                            break;
                        case "friends":
                            myFragment = new FriendFragment();
                            break;
                        case "settings":
                            myFragment = new SettingsFragment();
                            break;
                        default:
                            myFragment = new VideoFragment();
                            break;
                    }
                    fragmentTransaction.add(i.getValue(), myFragment,i.getKey());
                }
                fragmentTransaction.commit();
                page_panel.requestLayout();
                scroll_page.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(HomePageViewModel.class);
        viewModel.getHomePageContent().observe(this, new Observer<HomePageContent>() {
            @Override
            public void onChanged(HomePageContent homePageContent) {
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

        if (!newBie) {
            viewModel.loadHomePageContent(this);
        }else{
            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    FrameLayout container = new FrameLayout(HomePage.this);
                    container.setId(View.generateViewId());
                    root.addView(container);
                    fragmentTransaction.add(container.getId(), SetUpInformationFragment.newInstance(), null);
                    fragmentTransaction.commit();
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
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

    public HomePageViewModel getViewModel() {
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

    public void requestUpdateAvatar() {
        pickAvatar.launch("image/*");
    }

    public void requestUpdateBackground() {
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "view profile" + Integer.toString(container.getId());
        fragmentTransaction.add(container.getId(), new ViewProfileFragment(author), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
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

    public void openCommentFragment(PostItemViewModel post) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "comment";
        fragmentTransaction.replace(container.getId(), CommentFragment.newInstance(post), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }
    public void openSearchFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "search";
        fragmentTransaction.replace(container.getId(), SearchFragment.newInstance(), tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void finishFragment(String tag) {
        Fragment top = findTopFragment();
        if (top != null && top.getTag().startsWith(tag)) {
            onBackPressed();
        }
    }

    public void openViewImageFragment(Rect bound, ImagePost postItem) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        root.addView(container);
        String tag = "view image" + Integer.toString(container.getId());
        fragmentTransaction.add(container.getId(), new ViewImageFragment(bound, postItem), tag);
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