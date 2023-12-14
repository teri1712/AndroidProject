package com.example.socialmediaapp.customview.textview;

import android.content.Context;
import android.graphics.Color;
import android.text.BoringLayout;
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

import com.example.socialmediaapp.application.session.ReplyCommentSessionHandler;
import com.example.socialmediaapp.viewmodel.dunno.LikeHelper;

public class LikeTextView extends ClickableTextView {
    private String actionState;
    private LifecycleOwner lifecycleOwner;
    private MutableLiveData<Boolean> likeLiveData;
    private LikeHelper likeHelper;

    public void initLikeView(LifecycleOwner lifecycleOwner, MutableLiveData<Boolean> likeLiveData) {
        this.likeLiveData = likeLiveData;
        this.lifecycleOwner = lifecycleOwner;
        actionState = "Idle";
        likeLiveData.observe(lifecycleOwner, isActive -> {
            if (isActive == null) return;
            if (!isActive) {
                setTextColor(Color.parseColor("#757575"));
            } else {
                setTextColor(Color.parseColor("#0866FF"));
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

    public void setLikeHelper(LikeHelper likeHelper) {
        this.likeHelper = likeHelper;
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

    public LikeTextView(@NonNull Context context) {
        super(context);
    }

    public LikeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface Action {
        MutableLiveData<String> activeAction(boolean isActive);
    }
}
