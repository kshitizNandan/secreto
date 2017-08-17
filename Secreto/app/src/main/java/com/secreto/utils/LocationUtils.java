package com.secreto.utils;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private static final String TAG = LocationUtils.class.getSimpleName();

    public static Address getAddressFromGeoCoder(Activity activity, double latitude, double longitude) {
        Logger.d(TAG, "getAddressFromGeoCoder lat : " + latitude + "lng : " + longitude);
        Address returnedAddress = null;
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                returnedAddress = addresses.get(0);
                Logger.d(TAG, "getAddressFromGeoCoder : Address returned : " + returnedAddress);
            } else {
                Logger.d(TAG, "getAddressFromGeoCoder : No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "getAddressFromGeoCoder : Can not get Address!");
        }
        return returnedAddress;
    }

    public static String getCompleteAddressFunction(Address returnedAddress) {
        if (returnedAddress == null) {
            return "";
        }
        StringBuilder strReturnedAddress = new StringBuilder("");
        for (int i = 0; i < returnedAddress.getMaxAddressLineIndex()+1; i++) {
            strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
        }
        String strAdd = strReturnedAddress.toString();
        Logger.d(TAG, "getCompleteAddressFunction Address : " + strAdd);
        return strAdd;
    }
}
