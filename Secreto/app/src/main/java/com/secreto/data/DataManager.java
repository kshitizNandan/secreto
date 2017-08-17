package com.secreto.data;

import com.android.volley.Request;
import com.secreto.common.ApiConstants;
import com.secreto.data.volley.MultipartRequest;
import com.secreto.data.volley.RequestManagerApi;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.StatusMessage;
import com.secreto.model.UserResponse;
import com.secreto.utils.Logger;
import java.io.File;
import java.util.HashMap;
import static com.secreto.BuildConfig.BASE_SERVER_ADDRESS;

public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private static final String URL_LOGIN =BASE_SERVER_ADDRESS + "login";
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

    public void login(String email, String password, ResultListenerNG<UserResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.EMAIL_ID, email);
        params.put(ApiConstants.PASSWORD, password);
        makeRequest(Request.Method.POST, URL_LOGIN, params, UserResponse.class, resultListenerNG);
    }

    public void signUp(String firstName, String lastName, String email, String password, String mobile, ResultListenerNG<StatusMessage> resultListenerNG) {
    }
}