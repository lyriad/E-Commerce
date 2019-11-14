package com.lyriad.e_commerce.Sessions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import com.lyriad.e_commerce.Activities.LoginActivity;
import com.lyriad.e_commerce.Models.User;
import com.lyriad.e_commerce.Utils.Constants;

import java.util.Calendar;

public class UserSession {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context mContext;
    public static final String IS_LOGGED_IN = "isLoggedIn";
    public static final String FIRST_TIME_LOGGED_IN = "firstTimeLoggedIn";

    public static final String KEY_ID = "id";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_REGISTER_DATE = "createdAt";
    public static final String KEY_LAST_UPDATED_DATE = "lastUpdatedAt";

    public UserSession(Context mContext) {
        this.mContext = mContext;
        sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Constants.SHARED_PREFERENCES_PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createUserSession (User user) {

        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putLong(KEY_ID, user.getId());
        editor.putLong(KEY_TOKEN, user.getToken());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putBoolean(KEY_PROVIDER, user.isProvider());
        editor.putString(KEY_IMAGE, user.getImage().toString());
        editor.putLong(KEY_REGISTER_DATE, user.getRegisterDate().getTimeInMillis());
        editor.putLong(KEY_LAST_UPDATED_DATE, user.getLastUpdateDate().getTimeInMillis());

        editor.commit();
    }

    public void updateUserSession (User user) {

        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putBoolean(KEY_PROVIDER, user.isProvider());
        editor.putString(KEY_IMAGE, user.getImage().toString());
        editor.putLong(KEY_LAST_UPDATED_DATE, user.getLastUpdateDate().getTimeInMillis());

        editor.commit();
    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {
            Intent intent = new Intent(mContext, LoginActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(intent);
        }

    }

    public void logoutUser() {

        editor.putBoolean(IS_LOGGED_IN, false);
        editor.commit();

        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return sharedPreferences.getLong(KEY_ID, -1);
    }

    public long getUserToken() {
        return sharedPreferences.getLong(KEY_TOKEN, -1);
    }

    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public void setName(String name) {

        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public void setPhone(String phone) {

        editor.putString(KEY_PHONE, phone);
        editor.commit();
    }

    public Uri getImage() {
        return Uri.parse(sharedPreferences.getString(KEY_IMAGE, null));
    }

    public void setImage(String image) {

        editor.putString(KEY_IMAGE, image);
        editor.commit();
    }

    public boolean isProvider() { return sharedPreferences.getBoolean(KEY_PROVIDER, false);}

    public void setProvider(boolean provider) {

        editor.putBoolean(KEY_PROVIDER, provider);
        editor.commit();
    }

    public Calendar getRegisterDate() {
        Calendar date = Calendar.getInstance();
        long millis = sharedPreferences.getLong(KEY_REGISTER_DATE, -1);
        date.setTimeZone(Constants.TIME_ZONE);
        date.setTimeInMillis(millis);
        return millis == -1 ? null : date;
    }

    public Calendar getLastUpdateDate() {
        Calendar date = Calendar.getInstance();
        long millis = sharedPreferences.getLong(KEY_LAST_UPDATED_DATE, -1);
        date.setTimeZone(Constants.TIME_ZONE);
        date.setTimeInMillis(millis);
        return millis == -1 ? null : date;
    }

    public Boolean getFirstTime() {
        return sharedPreferences.getBoolean(FIRST_TIME_LOGGED_IN, true);
    }

    public void setFirstTime(Boolean n) {
        editor.putBoolean(FIRST_TIME_LOGGED_IN, n);
        editor.commit();
    }
}
