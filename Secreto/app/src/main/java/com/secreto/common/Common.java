package com.secreto.common;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.secreto.R;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.Logger;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    private static String TAG = Common.class.getSimpleName();

    public static boolean isOnline(Context context) {
        boolean result = false;
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                result = networkInfo.isConnected();
            }
        }
        return result;
    }


    public static BaseResponse getStatusMessage(VolleyError error) {
        if (error != null && error.networkResponse != null) {
            try {
                NetworkResponse response = error.networkResponse;
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                BaseResponse statusCode = new Gson().fromJson(json, BaseResponse.class);
                statusCode.setStatusCode(response.statusCode);
                return statusCode;
            } catch (UnsupportedEncodingException e) {
                Logger.e(TAG, "Exception : " + e);
            } catch (JsonSyntaxException e) {
                Logger.e(TAG, "Exception : " + e);
            }
        }
        return null;
    }

    public static boolean isValidMobile(String mobile) {
        return (!TextUtils.isEmpty(mobile) && mobile.trim().length() == 9);
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidName(String name) {
        String NAME_PATTERN = "^(?=.*[A-Za-z'])[A-Za-z']{1,}$";
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[#?!@$%^&*-~]).{6,15}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    public static int dipToPixel(Context context, float dip) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float logicalDensity = metrics.density;
        int px = (int) (dip * logicalDensity + 0.5);
        return px;
    }

    public static Point getDeviceWidthHeight() {
        final WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getFormattedDistance(String miles) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setMinimumFractionDigits(1);
        Double aDouble = Double.parseDouble(miles);
        return numberFormat.format(aDouble);
    }

    public static void showAlertDialog(Context context, String message, final DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alert))
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), onClickListener)
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
