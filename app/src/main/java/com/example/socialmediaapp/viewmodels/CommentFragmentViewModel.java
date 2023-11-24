package com.example.socialmediaapp.viewmodels;

import android.net.Uri;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class CommentFragmentViewModel extends ViewModel {

    private MutableLiveData<Uri> image;

    private MutableLiveData<String> commentContent;

    private MediatorLiveData<Integer> cntEditedContent;

    private Integer postId;

    public CommentFragmentViewModel() {
        super();
        commentContent = new MutableLiveData<>();
        image = new MutableLiveData<>();
        cntEditedContent = new MediatorLiveData<>();
        cntEditedContent.setValue(0);
        cntEditedContent.addSource(commentContent, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                int cur = cntEditedContent.getValue();
                if (s.isEmpty()) {
                    cur ^= cntEditedContent.getValue() & 1;
                } else {
                    cur |= 1;
                }
                cntEditedContent.setValue(cur);
            }
        });
        cntEditedContent.addSource(image, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                int cur = cntEditedContent.getValue();
                if (uri == null) {
                    cur ^= cntEditedContent.getValue() & 2;
                } else {
                    cur |= 2;
                }
                cntEditedContent.setValue(cur);
            }
        });
    }


    public MutableLiveData<Uri> getImage() {
        return image;
    }

    public MutableLiveData<String> getCommentContent() {
        return commentContent;
    }

    public MediatorLiveData<Integer> getCntEditedContent() {
        return cntEditedContent;
    }


}
