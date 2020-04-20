package com.example.demo.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

public class Post {
    private String id;
    private String txt;
    private String time;
    private String userId;
    private List<String> commentsList;
    private List<String> likeList;
    private List<String> tagsList;

    public Post () {}

    public Post(String id, String txt, String time, String userId, List<String> tagsList) {
        this.id = id;
        this.txt = txt;
        this.time = time;
        this.userId = userId;
        this.tagsList = tagsList;
    }

    public Post(String id, String txt, String time, String userId, List<String> commentsList, List<String> likeList, List<String> tagsList) {
        this.id = id;
        this.txt = txt;
        this.time = time;
        this.userId = userId;
        this.commentsList = commentsList;
        this.likeList = likeList;
        this.tagsList = tagsList;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<String> commentsList) {
        this.commentsList = commentsList;
    }

    public List<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<String> likeList) {
        this.likeList = likeList;
    }

    public List<String> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<String> tagsList) {
        this.tagsList = tagsList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return id == post.id &&
                txt.equals(post.txt) &&
                time.equals(post.time) &&
                Objects.equals(commentsList, post.commentsList) &&
                Objects.equals(likeList, post.likeList) &&
                Objects.equals(tagsList, post.tagsList) &&
                userId.equals(post.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, txt, time, commentsList, likeList, tagsList, userId);
    }
}