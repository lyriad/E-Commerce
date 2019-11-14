package com.lyriad.e_commerce.Models;

import android.net.Uri;

public class Product {

    private String code;
    private String name;
    private double price;
    private Uri image;
    private Category category;
    private long userId;
    private boolean active;

    public Product() {

    }

    public Product(String code, String name, double price, Uri image, Category category, long userId, boolean active) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.userId = userId;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
