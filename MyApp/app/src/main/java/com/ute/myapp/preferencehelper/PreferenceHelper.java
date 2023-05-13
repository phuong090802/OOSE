package com.ute.myapp.preferencehelper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.model.User;

public class PreferenceHelper {
    private final SharedPreferences sharedPreferences;
    private static PreferenceHelper instance;

    private PreferenceHelper(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void saveCredential(String userName, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.KEY_USERNAME, userName);
        editor.putString(Constant.KEY_PASSWORD, password);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(Constant.KEY_USERNAME, null);
    }

    public String getPassword() {
        return sharedPreferences.getString(Constant.KEY_PASSWORD, null);
    }

    public void clearCredential() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constant.KEY_USERNAME);
        editor.remove(Constant.KEY_PASSWORD);
        editor.apply();
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constant.KEY_LOGGED_IN, loggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constant.KEY_LOGGED_IN, false);
    }

    public void saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.KEY_USER, json);
        editor.apply();
    }

    public User getUser() {
        String json = sharedPreferences.getString(Constant.KEY_USER, null);
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }
}
