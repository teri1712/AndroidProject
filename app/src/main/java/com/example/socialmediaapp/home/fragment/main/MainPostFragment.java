package com.example.socialmediaapp.home.fragment.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.SpinningFrame;
import com.example.socialmediaapp.customview.progress.PostLoading;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.models.MainPostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;

import java.util.List;

public class MainPostFragment extends Fragment {


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

    private MainPostFragmentViewModel viewModel;
    private View header_panel, header_frame;
    private ScrollView home_page_scroll;
    private View command_panel, command_frame;
    private CircleButton avatarButton, searchButton;
    private boolean loadInProgress;
    private HomePage homePage;
    private EditText create_post_edit_text;
    private SpinningFrame spinningFrame;
    private View postFragmentPanel;
    private PostFragment postFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_posts, container, false);
        header_panel = view.findViewById(R.id.header_panel);
        header_frame = view.findViewById(R.id.header_frame);
        avatarButton = view.findViewById(R.id.avatar_button);
        home_page_scroll = view.findViewById(R.id.home_scroll_pane);
        spinningFrame = view.findViewById(R.id.spinning_frame);
        create_post_edit_text = view.findViewById(R.id.open_create_post);
        postFragmentPanel = view.findViewById(R.id.post_fragment_container);
        homePage = (HomePage) getActivity();


        command_panel = homePage.getCommandPanel();
        command_frame = homePage.getCommandFrame();
        searchButton = view.findViewById(R.id.search_button);


        UserSessionViewModel userSessionViewModel = homePage.getViewModel();
        loadInProgress = false;
        userSessionViewModel.getUserInfo().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userInformation) {
                avatarButton.setOnClickListener(view1 -> {
                    UserBasicInfo userBasicInfo = new UserBasicInfo();
                    userBasicInfo.setFullname(userInformation.getFullname());
                    userBasicInfo.setAlias(userInformation.getAlias());
                    userBasicInfo.setAvatar(userSessionViewModel.getAvatar().getValue());
                    homePage.openViewProfileFragment(userBasicInfo);
                });
            }
        });
        userSessionViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), bitmap), 0);
            }
        });
        userSessionViewModel.getPostFragmentSession().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                viewModel = new MainPostFragmentViewModel(userSessionViewModel.getUserSessionHandler());
                init();
                createPostFragment((DataAccessHandler<Post>) sessionHandler);
            }
        });
        return view;
    }

    private void init() {
        initOnclick();
        spinningFrame.setAction(new Runnable() {
            @Override
            public void run() {
                viewModel.recyclePostFragment().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
                    @Override
                    public void onChanged(SessionHandler sessionHandler) {
                        createPostFragment((DataAccessHandler<Post>) sessionHandler);
                        spinningFrame.endLoading();
                    }
                });
            }
        });
    }

    private void createPostFragment(DataAccessHandler<Post> dataAccessHandler) {
        postFragment = new PostFragment(dataAccessHandler);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.post_fragment_container, postFragment, "posts");
        fragmentTransaction.commit();
    }

    private void initOnclick() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homePage.openSearchFragment();
            }
        });
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
                int h = postFragmentPanel.getHeight();
                int y = home_page_scroll.getScrollY();
                if (y + 2 * home_page_scroll.getHeight() > h) {
                    if (!loadInProgress) {
                        postFragment.getViewModel().loadPosts();
                        loadInProgress = true;
                    }
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

    public void onDestroyView() {
        super.onDestroyView();
        spinningFrame.endLoading();
    }

}
