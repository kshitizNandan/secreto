package com.secreto.widgets;

import android.content.Context;
import android.graphics.Typeface;


import com.secreto.utils.Logger;

import java.util.Hashtable;

public class Typefaces {
    private static final String TAG = Typefaces.class.getSimpleName();
    public static final String FONT_HELVETICA_BOLD = "fonts/HelveticaCondensedBold.otf";
    public static final String FONT_HELVETICA_REGULAR = "fonts/HelveticaCondensed.otf";
    public static final String FONT_KAUSHAN_REGULAR = "fonts/KaushanRegular.otf";

    private static final Hashtable<String, Typeface> cache = new Hashtable<>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Logger.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }
}
