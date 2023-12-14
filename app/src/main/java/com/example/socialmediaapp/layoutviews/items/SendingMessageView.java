package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.viewmodel.models.messenger.ImageMessageItem;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class SendingMessageView extends FrameLayout {
    private View textContainer;
    private TextView textView;
    private ImageView imageView;

    public SendingMessageView(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.sending_message_item, this, false);
        textContainer = root.findViewById(R.id.text_container);
        textView = root.findViewById(R.id.text_content);
        imageView = root.findViewById(R.id.image_content);
    }

    public void setImageContent(Uri uri) {
        imageView.setImageURI(uri);
        imageView.setVisibility(VISIBLE);
        textContainer.setVisibility(GONE);
    }

    public void setTextContent(String s) {
        textView.setText(s);
        textContainer.setVisibility(VISIBLE);
        imageView.setVisibility(GONE);
    }

}
