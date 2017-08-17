package com.secreto.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    private static final String TAG = DateFormatter.class.getSimpleName();
    private static final String DATE_FORMAT_DD_MM_YYYY = "dd-MM-yyyy";
    private static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_0000_00_00 = "0000-00-00";
    private static final String DATE_DD_MMM_YYYY = "dd-MMM-yyyy";


    public static String get_DATE_DD_MM_YYYY(String timeString) {
        if (TextUtils.equals(timeString, DATE_FORMAT_0000_00_00)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DateFormatter.DATE_FORMAT_YYYY_MM_DD, Locale.ENGLISH);
        Date date = new Date();
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            SimpleDateFormat requiredFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY, Locale.ENGLISH);
            return requiredFormat.format(date);
        }
        return timeString;
    }

    public static String get_DATE_DD_MM_YYYY(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat(DateFormatter.DATE_FORMAT_DD_MM_YYYY, Locale.ENGLISH);
        String strDate = format.format(calendar.getTime());
        return strDate;
    }

    public static String get_DATE_YYYY_MM_DD(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat(DateFormatter.DATE_FORMAT_DD_MM_YYYY, Locale.ENGLISH);
        Date date = new Date();
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            SimpleDateFormat requiredFormat = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD, Locale.ENGLISH);
            return requiredFormat.format(date);
        }
        return timeString;
    }

    public static String get_DATE_DD_MMM_YYYY(String timeString) {
        if (TextUtils.equals(timeString, DATE_FORMAT_0000_00_00)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DateFormatter.DATE_FORMAT_YYYY_MM_DD, Locale.ENGLISH);
        Date date = new Date();
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            SimpleDateFormat requiredFormat = new SimpleDateFormat(DATE_DD_MMM_YYYY, Locale.ENGLISH);
            return requiredFormat.format(date);
        }
        return timeString;
    }
}
