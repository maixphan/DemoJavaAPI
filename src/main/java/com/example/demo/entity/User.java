package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;
import java.util.Objects;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private List<String> friendsId;
    private List<String> request;

    public User() {}

    public User(String id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String id, String username, String email, String password, List<String> friendsId, List<String> request) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.friendsId = friendsId;
        this.request = request;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(List<String> friendsId) {
        this.friendsId = friendsId;
    }

    public List<String> getRequest() {
        return request;
    }

    public void setRequest(Set<String> friendsId) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(friendsId, user.friendsId) &&
                Objects.equals(request, user.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, friendsId, request);
    }
}