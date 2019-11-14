package com.lyriad.e_commerce.Models;

import android.net.Uri;

import java.io.Serializable;

public class Category implements Serializable {

    private long id;
    private long userId;
    private long token;
    private String name;
    private Uri image;
    private boolean active;

    public Category () {

    }

    public Category (long id) {
        this.id = id;
    }

    public Category (long id, long userId, long token, String name, Uri image, boolean active) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.name = name;
        this.image = image;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
