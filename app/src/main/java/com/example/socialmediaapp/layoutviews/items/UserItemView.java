package com.example.socialmediaapp.layoutviews.items;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.MessageHome;
import com.example.socialmediaapp.customview.button.UserActiveView;
import com.example.socialmediaapp.viewmodel.items.UserItemViewModel;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.messenger.OnlineChat;

public class UserItemView extends FrameLayout {
    private View root;
    private UserActiveView userActiveView;
    private TextView fullname;
    private UserItemViewModel viewModel;
    private LifecycleOwner lifecycleOwner;

    public UserItemView(Fragment owner) {
        super(owner.getContext());
        lifecycleOwner = owner.getViewLifecycleOwner();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        root = (ViewGroup) inflater.inflate(R.layout.user_item, this, false);
        addView(root);
        userActiveView = root.findViewById(R.id.user_active);
        fullname = root.findViewById(R.id.fullname);
    }

    public void initViewModel(ChatSessionModel chatSessionModel) {
        OnlineChat onlineChat = chatSessionModel.getOnlineChat();
        viewModel = new UserItemViewModel(onlineChat.getIsActive());

        LiveData<Boolean> isActive = viewModel.getIsActive();
        isActive.observe(lifecycleOwner, aBoolean -> {
            if (aBoolean) {
                userActiveView.setUserState(UserActiveView.ACTIVE);
            } else {
                //register time counter service
                userActiveView.setUserState(UserActiveView.INACTIVE);
            }
        });

        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageHome messageHome = (MessageHome) getContext();
                messageHome.openChatFragment(chatSessionModel);
            }
        });

    }
}
