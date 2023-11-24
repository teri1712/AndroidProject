package com.example.socialmediaapp.home.fragment;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.textview.PostContentTextVIew;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;

public class ViewImageFragment extends Fragment implements FragmentAnimation {

    private ImagePost postItem;
    private Rect bound;
    private ViewGroup commandContent;

    public ViewImageFragment(Rect bound, ImagePost postItem) {
        this.postItem = postItem;
        this.bound = bound;
    }

    //i store the this fragment state in activity saved state
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
    }

    private ImageView zoom_out_image;
    private ClickablePanel view_image_frame;
    private View command_panel;
    private boolean command_panel_is_enable;
    private View root;
    private PostContentTextVIew statusTextView;
    private TextView fullnameTextView;
    private View backgroundPanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            return null;
        }
        root = inflater.inflate(R.layout.fragment_view_image, container, false);
        zoom_out_image = (ImageView) root.findViewById(R.id.zoom_out_image);
        view_image_frame = (ClickablePanel) root.findViewById(R.id.view_image_frame);
        command_panel = root.findViewById(R.id.command_panel);
        statusTextView = (PostContentTextVIew) root.findViewById(R.id.status_content);
        fullnameTextView = (TextView) root.findViewById(R.id.fullname);
        backgroundPanel = root.findViewById(R.id.background_panel);
        commandContent = root.findViewById(R.id.command_content);
        command_panel_is_enable = true;
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        zoom_out_image.setImageDrawable(postItem.getImage());
        fullnameTextView.setText(postItem.getAuthor().getFullname());
        statusTextView.setStatusContent(postItem.getStatus());
        if (postItem.getStatus() == null) {
            commandContent.removeView(statusTextView);
            statusTextView = null;
        } else {
            statusTextView.setStatusContent(postItem.getStatus());
        }
        initOnClick();
        return root;
    }

    public void initOnClick() {
        view_image_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (command_panel_is_enable) {
                    command_panel.animate().alpha(0).setDuration(300).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            command_panel.setVisibility(View.GONE);
                        }
                    }).start();
                } else {
                    command_panel.setVisibility(View.VISIBLE);
                    command_panel.animate().alpha(1).setDuration(300).start();
                }
                command_panel_is_enable = !command_panel_is_enable;
            }
        });
    }


    @Override
    public void performEnd(Runnable endAction) {
        endAction.run();
    }

    @Override
    public void performStart() {
        // i don't know why when my imageview lost focus it resize the image somehow, i better option is firstly focus on another view
        view_image_frame.requestFocus();
        ViewGroup.LayoutParams params = zoom_out_image.getLayoutParams();
        if (bound == null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            command_panel.setVisibility(View.VISIBLE);
            zoom_out_image.setAlpha(0.5f);
            command_panel.animate().alpha(1).setDuration(150).start();
            backgroundPanel.animate().alpha(1).setDuration(100).start();
            zoom_out_image.post(new Runnable() {
                @Override
                public void run() {
                    zoom_out_image.animate().alpha(1).setDuration(200).start();
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
                command_panel.setVisibility(View.VISIBLE);
                command_panel.animate().alpha(1).setDuration(150).start();
            }
        }).start();
        root.setLayerType(View.LAYER_TYPE_HARDWARE,null);
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
                        root.setLayerType(View.LAYER_TYPE_NONE  ,null);
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