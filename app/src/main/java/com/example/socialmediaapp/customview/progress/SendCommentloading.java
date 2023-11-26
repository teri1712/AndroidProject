package com.example.socialmediaapp.customview.progress;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.AvatarView;

public class SendCommentloading extends FrameLayout {


    private ViewGroup root;
    private AvatarView avatarButton;
    private TextView fullname, content;
    private ImageView imageContent;
    private FrameLayout backgroundPanel;

    public SendCommentloading(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        root = (ViewGroup) inflater.inflate(R.layout.sending_comment_item, this, false);
        setBackgroundColor(Color.parseColor("#0F0866FF"));
        addView(root);
        fullname = root.findViewById(R.id.fullname);
        avatarButton = root.findViewById(R.id.avatar_button);
        content = root.findViewById(R.id.comment_content);
        imageContent = root.findViewById(R.id.image_view);
        imageContent = root.findViewById(R.id.image_view);
        backgroundPanel = root.findViewById(R.id.background_panel);
    }

    public void setAvatar(Drawable avatar) {
        avatarButton.setBackgroundContent(avatar, 0);
    }

    public void setFullname(String fn) {
        fullname.setText(fn);
    }

    public void setLoadingContent(String c, Uri image) {
        if (c != null && !c.isEmpty()) {
            content.setText(c);
            content.setVisibility(VISIBLE);
            backgroundPanel.setWillNotDraw(false);
        } else {
            content.setVisibility(GONE);
        }
        if (image != null) {
            imageContent.setImageURI(image);
            imageContent.setVisibility(VISIBLE);
        } else {
            imageContent.setVisibility(GONE);
        }
    }
}
