package com.example.socialmediaapp.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

import java.util.ArrayList;
import java.util.List;

public class PostFragmentViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private ItemRepository<Post> listPost;

    public PostFragmentViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        listPost = new ItemRepository<>();
    }

    public ItemRepository<Post> getListPost() {
        return listPost;
    }

    public SavedStateHandle getSavedStateHandle() {
        return savedStateHandle;
    }

    public MutableLiveData<String> loadPosts(Context context) {
        final MutableLiveData<List<Post>> res = new MutableLiveData<>();
        MutableLiveData<String> callBack = new MutableLiveData<>();
        listPost.getUpdateOnRepo().addSource(res, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (posts.isEmpty()) {
                    callBack.setValue("No posts loaded");
                    return;
                }
                callBack.setValue(Integer.toString(posts.size()) + " posts loaded");
                for (Post p : posts) {
                    listPost.addToEnd(p);
                }
            }
        });
        ServiceApi.loadPosts(context, res);
        return callBack;
    }


}
