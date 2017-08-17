package com.secreto.data;
import com.secreto.data.volley.MultipartRequest;
import com.secreto.data.volley.RequestManagerApi;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.utils.Logger;

import java.io.File;
import java.util.HashMap;

public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private static DataManager singleton;
    private DataManager() {
    }

    public static DataManager getInstance() {
        if (singleton == null) {
            singleton = new DataManager();
        }
        return singleton;
    }

    private static <T> void makeRequest(int method, final String url, HashMap<Object, Object> params, Class<T> className, ResultListenerNG<T> resultListenerNG) {
        Logger.d(TAG, "Url : " + url);
        Logger.d(TAG, "Params : " + params);
        MultipartRequest request = new MultipartRequest(method, url, params, className, resultListenerNG);
        RequestManagerApi.getInstance().addToRequestQueue(request);
    }

    private static <T> void makeMultipartRequest(final String url, HashMap<Object, Object> params, Class<T> className, ResultListenerNG<T> resultListenerNG, String filePartName, File image) {
        Logger.d(TAG, "Params : " + params);
        Logger.d(TAG, "Url : " + url);
        Logger.d(TAG, "Image : " + image);
        MultipartRequest request = new MultipartRequest(url, params, className, resultListenerNG, filePartName, image);
        RequestManagerApi.getInstance().addToRequestQueue(request);
    }

    private static <T> void makeMultipartRequest(final String url, HashMap<Object, Object> params, Class<T> className, ResultListenerNG<T> resultListenerNG, HashMap<String, File> fileHashMaps) {
        Logger.d(TAG, "Params : " + params);
        Logger.d(TAG, "Url : " + url);
        Logger.d(TAG, "Images map : " + fileHashMaps);
        MultipartRequest request = new MultipartRequest(url, params, className, resultListenerNG, fileHashMaps);
        RequestManagerApi.getInstance().addToRequestQueue(request);
    }

}