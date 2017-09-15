package com.secreto.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.secreto.data.DataManager;
import com.secreto.model.User;

public class SharedPreferenceManager {
    private static SharedPreferences sharedPreference;
    public static final String EMAIL = "email";
    public static final String PASS = "password";
    private static final String FILE_NAME = "PREFERENCE";
    private static final String USER_OBJECT = "user_object";
    private static final String USER_ID = "user_id";
    private static final String PUSH_NOTIFICATION_TOKEN = "PUSH_NOTIFICATION_TOKEN";
    private static final String PUSH_NOTIFICATION_SERVICE = "PUSH_NOTIFICATION_SERVICE";

    static {
        sharedPreference = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static User getUserObject() {
        return new Gson().fromJson(sharedPreference.getString(USER_OBJECT, ""), User.class);
    }

    public static void setUserObject(User userObject) {
        String json = new Gson().toJson(userObject);
        sharedPreference.edit().putString(USER_OBJECT, json).apply();
    }

    public static String getFcmToken() {
        return sharedPreference.getString(PUSH_NOTIFICATION_TOKEN, "");
    }

    public static void setFcmToken(String token) {
        sharedPreference.edit().putString(PUSH_NOTIFICATION_TOKEN, token).apply();
    }

    public static void removeUserOnLogout() {
        sharedPreference.edit().remove(USER_OBJECT).apply();
        sharedPreference.edit().remove(USER_ID).apply();
    }


    public static void sertLoginCredentials(String email, String pass) {
        sharedPreference.edit().putString(EMAIL, email).apply();
        sharedPreference.edit().putString(PASS, pass).apply();
    }


    public static String getLoginCredentials(String param) {
        if (!TextUtils.isEmpty(param)) {
            if (param.equalsIgnoreCase(EMAIL))
                return sharedPreference.getString(EMAIL, "");
            else if (param.equalsIgnoreCase(PASS))
                return sharedPreference.getString(PASS, "");
        }
        return "";
    }

    public static void removeLoginCredentials() {
        sharedPreference.edit().remove(EMAIL).apply();
        sharedPreference.edit().remove(PASS).apply();
    }

    public static void setNotificationService(boolean b) {
        sharedPreference.edit().putBoolean(PUSH_NOTIFICATION_SERVICE, b).apply();
    }

    public static boolean getNotificationService() {
        return sharedPreference.getBoolean(PUSH_NOTIFICATION_SERVICE, false);
    }
}
