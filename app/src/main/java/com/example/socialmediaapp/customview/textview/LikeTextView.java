package com.example.socialmediaapp.customview.textview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.Comment;

public class LikeTextView extends ClickableTextView {

    private String actionState;
    private LifecycleOwner lifecycleOwner;
    private MutableLiveData<Boolean> isLiked;
    private Comment comment;

    private Action clickAction;

    public void initLikeView(LifecycleOwner lifecycleOwner, MutableLiveData<Boolean> isLiked) {

        actionState = "Idle";
        this.isLiked = isLiked;
        this.comment = comment;
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
                    setTextColor(Color.parseColor("#757575"));
                } else {
                    setTextColor(Color.parseColor("#0866FF"));
                }
            }
        });
    }

    public void setClickAction(Action clickAction) {
        this.clickAction = clickAction;
    }

    private void performAction() {
        if (clickAction == null) return;
        actionState = "In progress";
        final boolean curActive = isLiked.getValue();

        MutableLiveData<String> res = clickAction.activeAction(curActive);
        res.observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    if (curActive != isLiked.getValue()) {
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
