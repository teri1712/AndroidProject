package com.example.socialmediaapp.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

import java.util.List;

public class ViewProfileViewModel extends ViewModel {
    private MutableLiveData<UserProfile> userProfileInfo;

    private ItemRepository<Post> listPost;
    private SavedStateHandle savedStateHandle;

    public ViewProfileViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        userProfileInfo = new MutableLiveData<>();
        listPost = new ItemRepository<>();
    }

    public ItemRepository<Post> getListPost() {
        return listPost;
    }

    public MutableLiveData<UserProfile> getUserProfileInfo() {
        return userProfileInfo;
    }

    public void loadProfile(Context context, String alias) {
        ServiceApi.loadProfile(context, alias, userProfileInfo);
    }

    public MutableLiveData<String> loadPosts(Context context) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        MutableLiveData<List<Post>> batchLoad = new MutableLiveData<>();
        listPost.getUpdateOnRepo().addSource(batchLoad, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> mutableLiveData) {
                if (mutableLiveData == null) {
                    callBack.setValue("Failed");
                    return;
                }
                if (mutableLiveData.isEmpty()) {
                    callBack.setValue("No profile post loaded");
                    return;
                }
                for (Post p : mutableLiveData) {
                    listPost.addToEnd(p);
                }
                callBack.setValue(Integer.toString(mutableLiveData.size()) + " posts loaded");
            }
        });
        ServiceApi.loadPostsOfUser(context, userProfileInfo.getValue().getAlias(), batchLoad);
        return callBack;
    }
}
