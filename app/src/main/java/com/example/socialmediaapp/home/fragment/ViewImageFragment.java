package com.example.socialmediaapp.home.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.LikeButton;
import com.example.socialmediaapp.customview.button.PostButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.container.DragPanel;
import com.example.socialmediaapp.customview.textview.PostContentTextVIew;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodel.dunno.LikeHelper;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.ImagePostViewModel;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;

public class ViewImageFragment extends Fragment implements FragmentAnimation {

    private Rect bound;
    private Bitmap image;
    private ViewGroup commandContent;

    public ViewImageFragment(Rect bound, Bitmap image) {
        this.bound = bound;
        this.image = image;
    }

    public ViewImageFragment() {
    }

    public static ViewImageFragment newInstance(Bundle args, Rect bound, Bitmap image) {
        ViewImageFragment fragment = new ViewImageFragment(bound, image);
        fragment.setArguments(args);
        return fragment;
    }

    private Integer postSessionId;

    //i store the this fragment state in activity saved state
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Bundle args = getArguments();
        postSessionId = args.getInt("session id");
    }

    private ImageView zoom_out_image;
    private ClickablePanel view_image_frame;
    private View command_panel;
    private boolean command_panel_is_enable;
    private DragPanel root;
    private PostContentTextVIew statusTextView;
    private TextView fullnameTextView;
    private View backgroundPanel;
    private ImagePostViewModel viewModel;
    private TextView cntTimeTextView, cntLikeTextView, cntCommentTextView;
    private LikeButton like;
    private PostButton comment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            return null;
        }
        root = (DragPanel) inflater.inflate(R.layout.fragment_view_image, container, false);
        zoom_out_image = root.findViewById(R.id.zoom_out_image);
        view_image_frame = root.findViewById(R.id.view_image_frame);
        command_panel = root.findViewById(R.id.command_panel);
        statusTextView = root.findViewById(R.id.status_content);
        fullnameTextView = root.findViewById(R.id.fullname);
        backgroundPanel = root.findViewById(R.id.background_panel);
        commandContent = root.findViewById(R.id.command_content);
        cntTimeTextView = root.findViewById(R.id.cnt_time);
        cntLikeTextView = root.findViewById(R.id.cnt_like);
        cntCommentTextView = root.findViewById(R.id.cnt_comment);
        like = root.findViewById(R.id.like_button);
        comment = root.findViewById(R.id.comment_button);
        root.setDragHelper(new DragPanel.ChildBoundDragHelper(view_image_frame));

        like.setWhite(true);
        command_panel_is_enable = true;
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                performStart();
            }
        });
        root.setDragListener(new DragPanel.DragListener() {
            @Override
            public void onStartDrag() {
                if (command_panel.getAlpha() != 0)
                    command_panel.setAlpha(0);
                if (backgroundPanel.getAlpha() != 0.9f) backgroundPanel.setAlpha(0.9f);
            }

            @Override
            public void onDrag() {
                float cur_stran = view_image_frame.getTranslationY();
                int h = view_image_frame.getHeight();
                float alpha = 1 - Math.abs(cur_stran) / h;
                root.setAlpha(alpha);
            }

            @Override
            public void onRelease() {
                backgroundPanel.animate().alpha(1f).setDuration(200);
                command_panel.animate().alpha(1f).setDuration(200);
                root.setAlpha(1);

            }

            @Override
            public void onFinish() {
                getActivity().getSupportFragmentManager().popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            @Override
            public void onAboutToFinish() {
                root.animate().alpha(0).setDuration(200).start();
            }
        });
        return root;
    }

    public void initOnClick() {
        viewModel.getViewCommentSessionId().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                comment.setOnClickListener(view -> {
                    HomePage activity = (HomePage) getActivity();

                    Bundle args = new Bundle();
                    args.putInt("session id", integer);
                    activity.openCommentFragment(args, viewModel.getTotalCountLike());
                });
            }
        });
        view_image_frame.setOnClickListener(view -> {
            if (command_panel_is_enable) {
                command_panel.animate().alpha(0).setDuration(300).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        command_panel.setVisibility(GONE);
                    }
                }).start();
            } else {
                command_panel.setVisibility(VISIBLE);
                command_panel.animate().alpha(1).setDuration(300).start();
            }
            command_panel_is_enable = !command_panel_is_enable;
        });
    }


    @Override
    public void performEnd(Runnable endAction) {
        ApplicationContainer.getInstance().sessionRepository.deleteIfDetached(postSessionId);
        endAction.run();
    }

    public void afterAnimation() {

        HomePage homePage = (HomePage) getActivity();
        viewModel = new ImagePostViewModel(postSessionId, homePage.getViewModel().getUserInfo().getValue().getFullname());

        viewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                ImagePost postItem = (ImagePost) post;
                zoom_out_image.setImageDrawable(new BitmapDrawable(getResources(), postItem.getImage()));
                fullnameTextView.setText(postItem.getAuthor().getFullname());
                statusTextView.setStatusContent(postItem.getStatus());
                if (postItem.getStatus() == null) {
                    commandContent.removeView(statusTextView);
                    statusTextView = null;
                } else {
                    statusTextView.setStatusContent(postItem.getStatus());
                }
                viewModel.getLiveData().removeObserver(this);
            }
        });
        viewModel.getTime().observe(getViewLifecycleOwner(), s -> cntTimeTextView.setText(s));
        viewModel.getCountLikeContent().observe(getViewLifecycleOwner(), s -> {
            if (s.isEmpty()) {
                cntLikeTextView.setVisibility(GONE);
            } else {
                cntLikeTextView.setText(s);
                if (cntLikeTextView.getVisibility() == GONE) {
                    cntLikeTextView.setVisibility(VISIBLE);
                }
            }
        });
        viewModel.getCountComment().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                cntCommentTextView.setVisibility(GONE);
            } else {
                String txt = Integer.toString(integer) + " comments";
                cntCommentTextView.setText(txt);
                if (cntCommentTextView.getVisibility() == GONE) {
                    cntCommentTextView.setVisibility(VISIBLE);
                }
            }
        });
        viewModel.getPostSessionHandler().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                sessionHandler.setRetain(true);
            }
        });
        viewModel.getPostSessionHandler().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                PostSessionHandler postSessionHandler = (PostSessionHandler) sessionHandler;
                like.initLikeView(getViewLifecycleOwner(), (MutableLiveData<Boolean>) viewModel.getLikeLiveData());
                like.setLikeHelper(new LikeHelper() {
                    @Override
                    public MutableLiveData<Boolean> getLikeSync() {
                        return postSessionHandler.getLikeSync();
                    }

                    @Override
                    public LiveData<String> doLike() {
                        return postSessionHandler.doLike();
                    }

                    @Override
                    public LiveData<String> doUnLike() {
                        return postSessionHandler.doUnLike();
                    }
                });
            }
        });

        initOnClick();

    }

    @Override
    public void performStart() {
        if (image == null) return;

        view_image_frame.requestFocus();
        zoom_out_image.setImageDrawable(new BitmapDrawable(getResources(), image));
        ViewGroup.LayoutParams params = zoom_out_image.getLayoutParams();
        if (bound == null) {
            //click on avatar
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            zoom_out_image.setAlpha(0.5f);
            backgroundPanel.animate().alpha(1).setDuration(100).start();
            zoom_out_image.post(new Runnable() {
                @Override
                public void run() {
                    zoom_out_image.animate().alpha(1).setDuration(200).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            afterAnimation();
                            command_panel.setVisibility(VISIBLE);
                            command_panel.animate().alpha(1).setDuration(150).start();
                        }
                    }).start();
                }
            });
            zoom_out_image.requestLayout();
            return;
        }

        int h = root.getHeight(), w = root.getWidth();
        params.width = (bound.right - bound.left);
        params.height = (bound.bottom - bound.top);
        int targetY = (h - params.height) / 2;
        int iniTranslationY = bound.top - targetY;
        zoom_out_image.setTranslationY(iniTranslationY);
        zoom_out_image.requestLayout();
        int time = 200 + Math.abs(targetY - bound.top) * 200 / Math.max(targetY, h - targetY);
        backgroundPanel.animate().alpha(1).setDuration(10).start();
        zoom_out_image.animate().translationY(0).setDuration(time).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                zoom_out_image.requestLayout();
                afterAnimation();
                command_panel.setVisibility(VISIBLE);
                command_panel.animate().alpha(1).setDuration(150).start();
            }
        }).start();
        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        Thread animator = new Thread(new Runnable() {
            double curHeight = params.height;
            double unitHeight = 1;
            int cntFrames = (int) ((h - curHeight) / unitHeight);

            @Override
            public void run() {
                for (int i = 0; i < cntFrames / 2; i++) {
                    try {
                        Thread.sleep(0, 50 * 1000000 / cntFrames);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    curHeight += unitHeight;
                    final int hei = (int) curHeight;
                    zoom_out_image.post(new Runnable() {
                        @Override
                        public void run() {
                            params.height = hei;
                            zoom_out_image.requestLayout();
                        }
                    });
                }
                zoom_out_image.post(new Runnable() {
                    @Override
                    public void run() {
                        root.setLayerType(View.LAYER_TYPE_NONE, null);
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        zoom_out_image.requestLayout();
                    }
                });
            }
        });
        animator.setPriority(Thread.MAX_PRIORITY);
        animator.start();
    }
}