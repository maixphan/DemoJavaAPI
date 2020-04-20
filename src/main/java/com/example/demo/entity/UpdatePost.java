package com.example.demo.entity;

import java.util.List;

public class UpdatePost {
    private String id;
    private String txt;
    private List<String> tagsList;
    private String userId;

    public UpdatePost(String id, String txt, List<String> tagsList, String userId) {
        this.id = id;
        this.txt = txt;
        this.tagsList = tagsList;
        this.userId = userId;
    }

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
}
