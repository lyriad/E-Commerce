package com.lyriad.e_commerce.Models;

import android.net.Uri;
import com.lyriad.e_commerce.Utils.Constants;

import java.util.Calendar;

public class User {

    private long id;
    private long token;
    private Uri image;
    private String name;
    private String username;
    private String email;
    private String phone;
    private boolean provider;
    private Calendar registerDate;
    private Calendar lastUpdateDate;

    public User () { }

    public User(long id, long token, Uri image, String name, String username, String email, String phone, boolean provider, long registerDateMillis, long lastUpdateDateMillis) {
        this.id = id;
        this.token = token;
        this.image = image;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.provider = provider;
        this.registerDate = Calendar.getInstance();
        this.registerDate.setTimeZone(Constants.TIME_ZONE);
        this.registerDate.setTimeInMillis(registerDateMillis);
        this.lastUpdateDate = Calendar.getInstance();
        this.lastUpdateDate.setTimeZone(Constants.TIME_ZONE);
        this.lastUpdateDate.setTimeInMillis(lastUpdateDateMillis);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getToken() {
        return token;
    }

    public void setToken(long token) {
        this.token = token;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isProvider() {
        return provider;
    }

    public void setProvider(boolean provider) {
        this.provider = provider;
    }

    public Calendar getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Calendar registerDate) {
        this.registerDate = registerDate;
    }

    public Calendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Calendar lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
