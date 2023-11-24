package com.example.socialmediaapp.customview.button;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

public class LikeButton extends PostButton {

    private String actionState;
    private LifecycleOwner lifecycleOwner;
    private MutableLiveData<Boolean> isLiked;
    private Post post;

    public void initLikeAction(LifecycleOwner lifecycleOwner, MutableLiveData<Boolean> isLiked, Post post) {

        actionState = "Idle";
        this.isLiked = isLiked;
        this.post = post;
        this.lifecycleOwner = lifecycleOwner;

        isLiked.observe(lifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b == null) return;
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isLiked.setValue(!isLiked.getValue());
                        if (actionState.equals("Idle")) {
                            performAction();
                        }
                    }
                });
                isLiked.removeObserver(this);
            }
        });
        isLiked.observe(lifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isActive) {
                if (isActive == null) return;
                if (!isActive) {
                    setBackgroundContent(getResources().getDrawable(R.drawable.like_24, null));
                    setTextContentColor(Color.parseColor("#757575"));

                } else {
                    setBackgroundContent(getResources().getDrawable(R.drawable.active_like_24, null));
                    setTextContentColor(Color.parseColor("#0866FF"));
                }
            }
        });
    }

    private void performAction() {
        actionState = "In progress";

        MutableLiveData<String> res = new MutableLiveData<>();
        final boolean curActive = isLiked.getValue();
        res.observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    if (curActive != isLiked.getValue()) {
                        //retry
                        post(new Runnable() {
                            @Override
                            public void run() {
                                performAction();
                            }
                        });
                    } else {
                    }
                } else {
                    isLiked.setValue(!curActive);
                    Toast.makeText(getContext(), "Error occurs please try again.", Toast.LENGTH_SHORT).show();
                }
                actionState = "Idle";
            }
        });

        if (curActive) {
            ServiceApi.likePost(post.getId(), res);
        } else {
            ServiceApi.unlikePost(post.getId(), res);
        }
    }

    public LikeButton(@NonNull Context context) {
        super(context);
    }

    public LikeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
