package com.secreto.utils;

import android.text.TextUtils;
import android.util.Log;

import com.secreto.R;
import com.secreto.common.MyApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {
    private static final String TAG = DateFormatter.class.getSimpleName();
    private static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String MMM_DD_YYYY = "dd,MMM yy";

    public static String getTime(final long timestamp) {
        Logger.d(TAG, "getTime date: " + timestamp);
        String actionTimeStr = "";
        final Calendar current = Calendar.getInstance();
        final Calendar before = Calendar.getInstance();
        before.setTimeInMillis(timestamp);
        // The getTimeInMillis shows a diff which is exactly that of the timezone.
        Logger.d(TAG, "current: " + current.getTimeInMillis());
        Logger.d(TAG, "before: " + before.getTimeInMillis());

        final long minutesAgo = current.get(Calendar.MINUTE) - before.get(Calendar.MINUTE);
        long daysago = current.get(Calendar.DAY_OF_YEAR) - before.get(Calendar.DAY_OF_YEAR);
        // Less than a day ago.
        if (daysago == 0) {
            final long hoursago = current.get(Calendar.HOUR_OF_DAY) - before.get(Calendar.HOUR_OF_DAY);
            Logger.d(TAG, "hoursago: " + hoursago);
            if (hoursago == 1) {
                actionTimeStr = MyApplication.getInstance().getResources().getQuantityString(R.plurals.hour_plural, (int) hoursago, hoursago);
            } else if (hoursago != 0) {
                actionTimeStr = (hoursago < 0) ? MyApplication.getInstance().getString(R.string.just_now) : MyApplication.getInstance().getResources().getQuantityString(R.plurals.hour_plural, (int) hoursago, hoursago);
            } else {
                final long minutes = current.get(Calendar.MINUTE) - before.get(Calendar.MINUTE);
                Logger.d(TAG, "minutes: " + minutes);
                actionTimeStr = (minutes < 1) ? MyApplication.getInstance().getString(R.string.just_now) : MyApplication.getInstance().getResources().getQuantityString(R.plurals.minute_plural, (int) minutes, minutes);
            }
        } else if (daysago == 1) {
            return MyApplication.getInstance().getString(R.string.yesterday);
        } else if (daysago >= 2 && daysago <= 6) {
            int dayNumber = before.get(Calendar.DAY_OF_WEEK);
            if (dayNumber == 1)
                return MyApplication.getInstance().getString(R.string.sunday);
            if (dayNumber == 2)
                return MyApplication.getInstance().getString(R.string.monday);
            if (dayNumber == 3)
                return MyApplication.getInstance().getString(R.string.tuesday);
            if (dayNumber == 4)
                return MyApplication.getInstance().getString(R.string.wednesday);
            if (dayNumber == 5)
                return MyApplication.getInstance().getString(R.string.thursday);
            if (dayNumber == 6)
                return MyApplication.getInstance().getString(R.string.friday);
            if (dayNumber == 7)
                return MyApplication.getInstance().getString(R.string.saturday);
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MMM_DD_YYYY, Locale.getDefault());
            actionTimeStr = simpleDateFormat.format(before.getTime());
        }
        return actionTimeStr;
    }

    public static String getTimeString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return getTime(date.getTime());
        }
        return dateString;
    }
}
