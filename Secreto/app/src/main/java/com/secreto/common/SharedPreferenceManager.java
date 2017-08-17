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

    public static SharedPreferences getSharedPreferences() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference;
    }

    public static User getUserObject() {
        Gson gson = new Gson();
        return gson.fromJson(getSharedPreferences().getString(USER_OBJECT, ""), User.class);
    }

    public static void setUserObject(User userObject) {
        Gson gson = new Gson();
        String json = gson.toJson(userObject);
        getSharedPreferences().edit().putString(USER_OBJECT, json).apply();
    }

    public static String getUserId() {
        return getSharedPreferences().getString(USER_ID, "");
    }

    public static void setUserId(String userId) {
        getSharedPreferences().edit().putString(USER_ID, userId).apply();
    }

    public static String getFcmToken() {
        return getSharedPreferences().getString(PUSH_NOTIFICATION_TOKEN, "");
    }

    public static void setFcmToken(String token) {
        getSharedPreferences().edit().putString(PUSH_NOTIFICATION_TOKEN, token).apply();
    }

    public static void removeUserOnLogout() {
        getSharedPreferences().edit().remove(USER_OBJECT).apply();
        getSharedPreferences().edit().remove(USER_ID).apply();
    }

    public static boolean isTutorialComplete() {
        return getSharedPreferences().getBoolean(IS_TUTORIAL_COMPLETE, false);
    }

    public static void setIsTutorialComplete(boolean isTutorialComplete) {
        getSharedPreferences().edit().putBoolean(IS_TUTORIAL_COMPLETE, isTutorialComplete).apply();
    }
}
