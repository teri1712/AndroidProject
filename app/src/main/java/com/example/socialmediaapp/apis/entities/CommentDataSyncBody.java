package com.example.socialmediaapp.apis.entities;

public class CommentDataSyncBody {
    private Integer countLike, countComment;

    public CommentDataSyncBody() {
    }

    public Integer getCountLike() {
        return countLike;
    }

    public void setCountLike(Integer countLike) {
        this.countLike = countLike;
    }

    public Integer getCountComment() {
        return countComment;
    }

    public void setCountComment(Integer countComment) {
        this.countComment = countComment;
    }

}
