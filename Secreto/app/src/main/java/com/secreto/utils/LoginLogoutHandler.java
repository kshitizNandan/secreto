package com.secreto.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.activities.LoginActivity;
import com.secreto.common.Common;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;


public class LoginLogoutHandler {

    private static final String TAG = LoginLogoutHandler.class.getSimpleName();

    public static void storeUserIntoPrefs(User userObject) {
        SharedPreferenceManager.setUserObject(userObject);
    }

    public static void logoutUser(final Context context) {
        logoutUserApiCall(context);
    }

    public static void logoutUserWithConfirm(final Context context) {
        Common.showAlertDialog(context, context.getString(R.string.sure_to_logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logoutUserApiCall(context);
            }
        }, true);
    }

    private static void logoutUserApiCall(final Context context) {
        if (Common.isOnline(context)) {
            final CustomProgressDialog progressDialog = new CustomProgressDialog(context);
            DataManager.getInstance().logoutApiCall(new ResultListenerNG<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse response) {
                    progressDialog.dismiss();
                    SharedPreferenceManager.removeUserOnLogout();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Logger.e(TAG, "Logout error : " + error.getMessage());
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "Logout error : " + baseResponse.getMessage());
                        Toast.makeText(context, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(context, context.getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
