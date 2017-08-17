package com.secreto.utils;

import android.content.Context;
import android.content.Intent;

import com.secreto.activities.SplashScreenActivity;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.model.User;


public class LoginLogoutHandler {

    public static void storeUserIntoPrefs(User userObject) {
        SharedPreferenceManager.setUserObject(userObject);
        SharedPreferenceManager.setUserId(userObject.getUserId());
    }

    public static void logoutUser(Context context) {
        SharedPreferenceManager.removeUserOnLogout();
        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}
