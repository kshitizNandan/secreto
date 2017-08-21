package com.secreto.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.secreto.R;
import com.secreto.activities.LoginActivity;
import com.secreto.activities.SplashScreenActivity;
import com.secreto.common.Common;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.model.User;


public class LoginLogoutHandler {

    public static void storeUserIntoPrefs(User userObject) {
        SharedPreferenceManager.setUserObject(userObject);
        SharedPreferenceManager.setUserId(userObject.getUserId());
    }

    public static void logoutUser(final Context context) {
        Common.showAlertDialog(context, context.getString(R.string.sure_to_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceManager.removeUserOnLogout();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        });
    }
}
