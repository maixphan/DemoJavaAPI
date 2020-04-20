package com.example.demo.entity;

import java.util.List;

public class NewComment {
    private String postId;
    private String txt;
    private String userId;

    public NewComment(String postId, String txt, String userId) {
        this.postId = postId;
        this.txt = txt;
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
