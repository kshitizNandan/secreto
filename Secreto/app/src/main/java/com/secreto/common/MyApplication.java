package com.secreto.common;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;


import com.secreto.widgets.Typefaces;
import com.secreto.image.ImageCacheManager;
import com.secreto.image.RequestManager;
import com.secreto.utils.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication singleton;
    // Fonts
    public static Typeface fontHelveticaBold;
    public static Typeface fontHelveticaRegular;

    // For Image Loader Cache
    private static int MEM_CACHE_SIZE = 100; // Number of "screens" to cache.
    private static Bitmap.CompressFormat MEM_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int MEM_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided

    public static synchronized MyApplication getInstance() {
        return singleton;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.v(TAG, "onCreate");
        RequestManager.init(this);
        singleton = this;
        createImageCache();
        preloadTypefaces();
        printHashKey();

    }

    private void preloadTypefaces() {
        Logger.d(TAG, "preloadTypefaces");
        fontHelveticaBold = Typefaces.get(getApplicationContext(), Typefaces.FONT_HELVETICA_BOLD);
        fontHelveticaRegular = Typefaces.get(getApplicationContext(), Typefaces.FONT_HELVETICA_REGULAR);
    }

    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Logger.d(TAG, "TEMPHASH KEY:" +
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    private void createImageCache() {
        final int mem_imagecache_size = getScreenSizeScale() * MEM_CACHE_SIZE;

        ImageCacheManager.getInstance().init(this, this.getPackageCodePath(), mem_imagecache_size, MEM_IMAGECACHE_COMPRESS_FORMAT, MEM_IMAGECACHE_QUALITY);
    }
    /**
     * Get the screen pixel count
     */
    private int getScreenSizeScale() {
        final Point size = getScreenSize();
        int width = size.x;
        int height = size.y;
        return height * width;
    }

    public static Point getScreenSize() {
        final WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        return size;
    }
}