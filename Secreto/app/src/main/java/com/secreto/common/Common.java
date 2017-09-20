package com.secreto.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import com.google.gson.Gson;
import com.secreto.R;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.Logger;

import java.io.File;
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
        if (error instanceof TimeoutError) {
            Logger.e(TAG, "TimeoutError");
        } else if (error instanceof NoConnectionError) {
            Logger.e(TAG, "NoConnectionError");
        } else if (error instanceof AuthFailureError) {
            Logger.e(TAG, "AuthFailureError");
        } else if (error instanceof ServerError) {
            Logger.e(TAG, "ServerError");
        } else if (error instanceof NetworkError) {
            Logger.e(TAG, "NetworkError");
        } else if (error instanceof ParseError) {
            Logger.e(TAG, "ParseError");
        }
        if (error != null && error.networkResponse != null) {
            try {
                NetworkResponse response = error.networkResponse;
                String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                BaseResponse statusCode = new Gson().fromJson(json, BaseResponse.class);
                statusCode.setStatusCode(response.statusCode);
                return statusCode;
            } catch (Exception e) {
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

    public static void showAlertDialog(Context context, String message, final DialogInterface.OnClickListener onClickListener, boolean isShowCancelBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), onClickListener);
        if (isShowCancelBtn) {
            builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }

    public static void showAlertDialog(Context context, String message, final DialogInterface.OnClickListener okListener, final DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), okListener)
                .setNegativeButton(context.getString(R.string.cancel), cancelListener).show();
    }

    public static void shareProfile(Context context) {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://rajanpatelmakeupartist.com/secreto/" + SharedPreferenceManager.getUserObject().getUserName());
        context.startActivity(Intent.createChooser(sendIntent, "Share link!"));
    }
    public Bitmap drawTextToBitmap(Context mContext, int resourceId, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110, 110, 110));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 6;
            int y = (bitmap.getHeight() + bounds.height()) / 5;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }

}
