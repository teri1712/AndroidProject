package com.example.socialmediaapp.customview.button;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.viewmodel.dunno.LikeHelper;

public class LikeButton extends PostButton {

    private String actionState;
    private LifecycleOwner lifecycleOwner;
    private MutableLiveData<Boolean> likeLiveData;
    private LikeHelper likeHelper;
    private boolean white = false;
    public void setWhite(boolean white) {
        this.white = white;
    }

    public void initLikeView(LifecycleOwner lifecycleOwner, MutableLiveData<Boolean> likeLiveData) {
        actionState = "Idle";
        this.lifecycleOwner = lifecycleOwner;
        this.likeLiveData = likeLiveData;
        likeLiveData.observe(lifecycleOwner, isActive -> {
            if (!isActive) {
                setBackgroundContent(getResources().getDrawable(white ? R.drawable.white_like_24 : R.drawable.like_24, null));
                setTextContentColor(white ? Color.WHITE : Color.parseColor("#757575"));

            } else {
                setBackgroundContent(getResources().getDrawable(R.drawable.active_like_24, null));
                setTextContentColor(Color.parseColor("#0866FF"));
            }
        });
        likeLiveData.observe(lifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setOnClickListener(view -> {
                    likeLiveData.setValue(!likeLiveData.getValue());
                    if (actionState.equals("Idle")) {
                        performAction();
                    }
                });
                likeLiveData.removeObserver(this);
            }
        });
    }

    private void performAction() {
        actionState = "In progress";

        final boolean curActive = likeLiveData.getValue();
        LiveData<String> res = curActive ? likeHelper.doLike() : likeHelper.doUnLike();
        res.observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    if (curActive != likeLiveData.getValue()) {
                        post(() -> performAction());
                        return;
                    }
                    likeHelper.getLikeSync().setValue(curActive);
                } else {
                    likeLiveData.setValue(!curActive);
                    Toast.makeText(getContext(), "Error occurs please try again.", Toast.LENGTH_SHORT).show();
                }
                actionState = "Idle";
            }
        });

    }

    public void setLikeHelper(LikeHelper likeHelper) {
        this.likeHelper = likeHelper;
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

    public interface Action {
        MutableLiveData<String> activeAction(boolean isActive);
    }
}
