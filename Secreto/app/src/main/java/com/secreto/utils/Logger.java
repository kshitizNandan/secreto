package com.secreto.utils;

import android.util.Log;

import com.secreto.BuildConfig;
import com.secreto.R;
import com.secreto.common.MyApplication;


public class Logger {

    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(MyApplication.getInstance().getString(R.string.app_name), message);
        }
    }

    public static void e(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void v(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void e(String TAG, String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, e);
        }
    }
}
