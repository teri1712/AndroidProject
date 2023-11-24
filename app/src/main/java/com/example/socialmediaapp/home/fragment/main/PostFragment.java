package com.example.socialmediaapp.home.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.PostLoading;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodels.HomePageViewModel;
import com.example.socialmediaapp.viewmodels.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodels.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodels.models.HomePageContent;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;
import com.example.socialmediaapp.viewmodels.models.repo.Update;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodels.models.user.UserInformation;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {


    public PostFragment() {
    }

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private View header_panel, header_frame;
    private ScrollView home_page_scroll;
    private View command_panel, command_frame;
    private ViewGroup postPanel;
    private PostFragmentViewModel viewModel;
    private CircleButton avatarButton, searchButton;
    private CustomSpinningView loadSpinner;

    private PostLoading postLoading;

    public PostFragmentViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        header_panel = view.findViewById(R.id.header_panel);
        header_frame = view.findViewById(R.id.header_frame);
        avatarButton = view.findViewById(R.id.avatar_button);
        home_page_scroll = view.findViewById(R.id.home_scroll_pane);
        loadSpinner = view.findViewById(R.id.load_spinner);
        HomePage activity = (HomePage) getActivity();

        command_panel = activity.getCommandPanel();
        command_frame = activity.getCommandFrame();
        postPanel = view.findViewById(R.id.posts_panel);
        searchButton = view.findViewById(R.id.search_button);
        postLoading = new PostLoading(getContext());

        loadSpinner.setVisibility(View.VISIBLE);

        HomePageViewModel vm = activity.getViewModel();
        vm.getUserInfo().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userSession) {
                avatarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserBasicInfo self = new UserBasicInfo();
                        self.setAlias(userSession.getAlias());
                        self.setFullname(userSession.getFullname());
                        if (vm.getAvatarPost().getValue() != null) {
                            self.setAvatar(vm.getAvatarPost().getValue().getImage());
                        }
                        activity.openViewProfileFragment(self);
                    }
                });
            }
        });
        vm.getHomePageContent().observe(getViewLifecycleOwner(), new Observer<HomePageContent>() {
            @Override
            public void onChanged(HomePageContent homePageContent) {
                if (loadSpinner != null) {
                    loadSpinner.setVisibility(View.GONE);
                    ViewGroup vg = (ViewGroup) loadSpinner.getParent();
                    vg.removeView(loadSpinner);
                    loadSpinner = null;
                }
                performLoading();
                viewModel.loadPosts(getContext()).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s.equals("Success")) {
                        } else {
                        }
                        finishLoading();
                    }
                });
            }
        });
        vm.getAvatarPost().observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                if (imagePost == null) return;
                avatarButton.setBackgroundContent(imagePost.getImage(), 0);
            }
        });

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(PostFragmentViewModel.class);

        viewModel.getListPost().getUpdateOnRepo().observe(getViewLifecycleOwner(), new Observer<Update<Post>>() {
            @Override
            public void onChanged(Update<Post> p) {
                if (p == null) return;
                if (p.getOp() == Update.Op.ADD) {
                    PostItemView newPostItem = new PostItemView(PostFragment.this, viewModel.getListPost(), p.getPos());
                    postPanel.addView(newPostItem, p.getPos());
                } else {
                    postPanel.removeViewAt(p.getPos());
                }
            }
        });
        initOnclick(view);
        return view;
    }

    private void performLoading() {
        postLoading.start();
        postPanel.addView(postLoading);
    }

    private void finishLoading() {
        postLoading.cancel();
        postPanel.removeView(postLoading);
    }

    private void initOnclick(View root) {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomePage homePage = (HomePage) getActivity();
                homePage.openSearchFragment();
            }
        });
        EditText create_post_edit_text = root.findViewById(R.id.open_create_post);
        create_post_edit_text.setKeyListener(null);
        create_post_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
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
            }
        });
        home_page_scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i1 == 0) {
                    header_panel.animate().alpha(1f).setDuration(100).start();
                    header_frame.animate().translationY(0).setDuration(100).start();
                    command_panel.animate().alpha(1f).setDuration(100).start();
                    command_frame.animate().translationY(0).setDuration(100).start();
                }
            }
        });
        home_page_scroll.setOnTouchListener(new View.OnTouchListener() {
            float prey = -1, prex = -1;
            boolean intercept_header_ani = false;
            int dist_for_header_appear = 0;
            float last_velo = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float y = event.getY();

                final float cur_trans_y = Math.abs(header_frame.getTranslationY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        prey = -1;
                        prex = -1;
                        boolean cache = intercept_header_ani;
                        if (cache) {
                            if (event.getAction() == MotionEvent.ACTION_UP)
                                home_page_scroll.getParent().requestDisallowInterceptTouchEvent(false);
                            intercept_header_ani = false;
                        }
                        if (last_velo > 30) {
                            header_panel.animate().alpha(1f).setDuration(100).start();
                            header_frame.animate().translationY(0).setDuration(100).start();
                            command_panel.animate().alpha(1f).setDuration(100).start();
                            command_frame.animate().translationY(0).setDuration(100).start();
                            break;
                        } else if (last_velo < -30) {
                            header_panel.animate().alpha(0.0f).setDuration(100).start();
                            header_frame.animate().translationY(-header_frame.getHeight()).setDuration(100).start();
                            command_panel.animate().alpha(0.0f).setDuration(100).start();
                            command_frame.animate().translationY(command_frame.getHeight()).setDuration(100).start();
                            break;
                        }

                        if (!cache) break;
                        if (cur_trans_y > header_frame.getHeight() / 2) {
                            header_panel.animate().alpha(0.0f).setDuration(100).start();
                            header_frame.animate().translationY(-header_frame.getHeight()).setDuration(100).start();
                            command_panel.animate().alpha(0.0f).setDuration(100).start();
                            command_frame.animate().translationY(command_frame.getHeight()).setDuration(100).start();
                        } else {
                            header_panel.animate().alpha(1f).setDuration(100).start();
                            header_frame.animate().translationY(0).setDuration(100).start();
                            command_panel.animate().alpha(1f).setDuration(100).start();
                            command_frame.animate().translationY(0).setDuration(100).start();
                            home_page_scroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    home_page_scroll.smoothScrollBy(0, (int) -cur_trans_y);
                                }
                            });
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (prey == -1) {
                            dist_for_header_appear = 0;
                            intercept_header_ani = false;
                            last_velo = 0;
                            break;
                        }
                        float target_trans_head = 0;
                        float target_trans_command = 0;
                        if (y < prey) {
                            target_trans_head = Math.max(-header_frame.getHeight(), header_frame.getTranslationY() + y - prey);
                            target_trans_command = Math.min(command_frame.getHeight(), command_frame.getTranslationY() + prey - y);
                            if (last_velo > 0) {
                                last_velo = y - prey;
                            } else {
                                last_velo = Math.min(last_velo, y - prey);
                            }
                        } else if (y >= prey) {
                            target_trans_head = Math.min(0, header_frame.getTranslationY() + y - prey);
                            target_trans_command = Math.max(0, command_frame.getTranslationY() + prey - y);
                            if (last_velo < 0) {
                                last_velo = y - prey;
                            } else {
                                last_velo = Math.max(last_velo, y - prey);
                            }
                        }
                        if (!intercept_header_ani) {
                            if (Math.abs(prex - event.getX()) / Math.abs(prey - event.getY()) > 0.8f || home_page_scroll.getHeight() >= home_page_scroll.getChildAt(0).getHeight()) {
                                dist_for_header_appear = 0;
                                break;
                            }
                            if (y > prey) dist_for_header_appear += y - prey;
                            boolean willIntercept = (cur_trans_y == 0 && target_trans_head != cur_trans_y);
                            willIntercept |= (cur_trans_y == header_frame.getHeight() && dist_for_header_appear >= 350);
                            if (!willIntercept) {
                                break;
                            }
                            home_page_scroll.getParent().requestDisallowInterceptTouchEvent(true);
                            intercept_header_ani = true;
                        }
                        header_frame.setTranslationY(target_trans_head);
                        command_frame.setTranslationY(target_trans_command);
                        float alpha = 1 - Math.abs(header_frame.getTranslationY()) / header_frame.getHeight();
                        header_panel.setAlpha(alpha);
                        command_panel.setAlpha(alpha);
                        if (target_trans_head == -header_frame.getHeight()) {
                            intercept_header_ani = false;
                            dist_for_header_appear = 0;
                        }
                        break;
                    default:
                        break;
                }
                if (event.getAction() != MotionEvent.ACTION_CANCEL && event.getAction() != MotionEvent.ACTION_UP) {
                    prey = event.getY();
                    prex = event.getX();
                }
                return false;
            }
        });
    }
}
