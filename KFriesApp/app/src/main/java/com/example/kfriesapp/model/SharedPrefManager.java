package com.example.kfriesapp.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.kfriesapp.LoginActivity;

public class SharedPrefManager {
    //the constants
    private static final String SHARED_PREF_NAME = "bookstoresharedpref";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_ROLE = "keyrole";
    private static SharedPrefManager mInstance;
    public static Context mCtx;
    private SharedPrefManager(Context context) {
        mCtx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    /**
     * method to let the user login
     * this method will store the user data in shared preferences
     * @param user
     */
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putInt(KEY_ID, user.getUserID());
        editor.apply();

    }
    /**
     * this method will checker whether user is already logged in or not.
     * return True if already logged in
     */
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }
    /**
     * this method will give the information of logged in user, retrieved from
     SharedPreferences
     */
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        User user = new User();
        user.setUserID(sharedPreferences.getInt(KEY_ID, 0));
        user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setToken(sharedPreferences.getString(KEY_TOKEN, null));
        user.setRole(sharedPreferences.getString(KEY_ROLE, null));
        return user;
    }
    /**
     * this method will logout the user. clear the SharedPreferences
     */
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

    public String userEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }
}