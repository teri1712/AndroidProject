package com.example.socialmediaapp.viewmodels.models.user.profile;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.viewmodels.HomePageViewModel;
import com.example.socialmediaapp.viewmodels.models.UserSession;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.user.UserInformation;
import com.example.socialmediaapp.viewmodels.models.user.profile.base.UserProfile;

public class SelfProfile extends UserProfile {

    private MutableLiveData<SelfProfile> delegateToLocalLiveData;

    @MainThread
    public SelfProfile(@NonNull Fragment owner, HomePageViewModel homePageViewModel) {
        super();
        delegateToLocalLiveData = new MutableLiveData<>(this);
        MutableLiveData<UserInformation> userInfo = homePageViewModel.getUserInfo();
        MutableLiveData<ImagePost> avatarPost = homePageViewModel.getAvatarPost();
        MutableLiveData<ImagePost> backgroundPost = homePageViewModel.getBackgroundPost();
        avatarPost.observe(owner, new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                SelfProfile.this.avatarPost = imagePost;
                delegateToLocalLiveData.setValue(SelfProfile.this);
            }
        });
        backgroundPost.observe(owner, new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                SelfProfile.this.backgroundPost = imagePost;
                delegateToLocalLiveData.setValue(SelfProfile.this);
            }
        });
        userInfo.observe(owner, new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation info) {
                fullname = info.getFullname();
                alias = info.getAlias();
                gender = info.getGender();
                birthday = info.getBirthday();
                delegateToLocalLiveData.setValue(SelfProfile.this);
            }
        });
    }


    public MutableLiveData<SelfProfile> getDelegateToLocalLiveData() {
        return delegateToLocalLiveData;
    }
}
