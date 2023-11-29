package com.example.socialmediaapp.viewmodel;

import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.HashMap;
import java.util.List;

public class PostFragmentViewModel extends ViewModel {

    private Repository<Post> postRepository;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private MediatorLiveData<Update<Post>> postUpdate;
    private MutableLiveData<Boolean> loadPostState;

    public PostFragmentViewModel(DataAccessHandler<Post> dataAccessHandler) {
        super();
        postRepository = new Repository<>(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();
        postUpdate = new MediatorLiveData<>();
        loadPostState = new MutableLiveData<>();
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public LiveData<SessionHandler> createPostSession(Post post) {
        PostSessionHandler postSessionHandler = new PostSessionHandler(post);
        MutableLiveData<Integer> postSessionId = sessionRegistry.bindSession(postSessionHandler);
        return Transformations.switchMap(postSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return ApplicationContainer.getInstance().sessionRepository.getSessionById(input);
            }
        });
    }

    public void deletePostSession(int postSessionId, int posInParent) {
        sessionRegistry.unBindSession(postSessionId);
        postUpdate.postValue(new Update<>(Update.Op.REMOVE, null, posInParent));
    }

    public LiveData<String> uploadPost(Bundle data) {
        LiveData<HashMap<String, Object>> result = postRepository.uploadNewItem(data);
        LiveData<String> callBack = Transformations.map(result, new Function<HashMap<String, Object>, String>() {
            @Override
            public String apply(HashMap<String, Object> input) {
                return (String) input.get("status");
            }
        });
        postUpdate.addSource(result, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                String status = (String) hashMap.get("status");
                Post item = (Post) hashMap.get("item");

                if (status.equals("Success")) {
                    postUpdate.setValue(new Update<>(Update.Op.ADD, item, 0));
                }
                postUpdate.removeSource(result);
            }
        });

        return callBack;
    }

    public void loadPosts() {
        loadPostState.setValue(true);
        Bundle query = new Bundle();
        MutableLiveData<List<Post>> callBack = postRepository.fetchNewItems(query);
        postUpdate.addSource(callBack, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                for (Post p : posts) {
                    postUpdate.setValue(new Update<>(Update.Op.ADD, p, -1));
                }
                postUpdate.removeSource(callBack);
                loadPostState.setValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getLoadPostState() {
        return loadPostState;
    }

    public MediatorLiveData<Update<Post>> getPostUpdate() {
        return postUpdate;
    }
}
