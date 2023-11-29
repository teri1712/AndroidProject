package com.example.socialmediaapp.layoutviews.profile;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.base.ProfileView;
import com.example.socialmediaapp.layoutviews.profile.model.Configurer;
import com.example.socialmediaapp.viewmodel.models.user.profile.NotMeProfile;

public class NotMeProfileView extends ProfileView {
    private RoundedButton blueButton, greyButton;

    public RoundedButton getBlueButton() {
        return blueButton;
    }

    public RoundedButton getGreyButton() {
        return greyButton;
    }

    private MutableLiveData<Configurer> configuration;

    @Override
    protected void initOnClick() {
        super.initOnClick();
        blueButton = root.findViewById(R.id.blue_button);
        greyButton = root.findViewById(R.id.grey_button);
        blueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                configuration.getValue().performActionLeft();
            }
        });
        greyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                configuration.getValue().performActionRight();
            }
        });
    }

    public NotMeProfileView(@NonNull Fragment owner) {
        super(owner, R.layout.stranger_profile);
        configuration = new MutableLiveData<>();
        configuration.observe(owner.getViewLifecycleOwner(), new Observer<Configurer>() {
            @Override
            public void onChanged(Configurer configurer) {
                configurer.configure();
            }
        });
    }

    public void changeConfiguration(Configurer configurer) {
        configuration.setValue(configurer);
    }
}
