package com.secreto.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.secreto.model.User;

public class SharedPreferenceManager {
    private static SharedPreferences sharedPreference;
    private static final String FILE_NAME = "PREFERENCE";
    private static final String USER_OBJECT = "user_object";
    private static final String USER_ID = "user_id";
    private static final String IS_TUTORIAL_COMPLETE = "isTutorialComplete";
    private static final String PUSH_NOTIFICATION_TOKEN = "PUSH_NOTIFICATION_TOKEN";

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

    public static String getUserId() {
        return sharedPreference.getString(USER_ID, "");
    }

    public static void setUserId(String userId) {
        sharedPreference.edit().putString(USER_ID, userId).apply();
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

    public static boolean isTutorialComplete() {
        return sharedPreference.getBoolean(IS_TUTORIAL_COMPLETE, false);
    }

    public static void setIsTutorialComplete(boolean isTutorialComplete) {
        sharedPreference.edit().putBoolean(IS_TUTORIAL_COMPLETE, isTutorialComplete).apply();
    }
}
