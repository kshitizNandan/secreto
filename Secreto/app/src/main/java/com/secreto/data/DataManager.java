package com.secreto.data;

import com.android.volley.Request;
import com.secreto.common.ApiConstants;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.volley.MultipartRequest;
import com.secreto.data.volley.RequestManagerApi;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.responsemodel.MediaResponse;
import com.secreto.responsemodel.SendOrReceivedMessageResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import static com.secreto.BuildConfig.BASE_SERVER_ADDRESS;

public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private static final String URL_LOGIN = BASE_SERVER_ADDRESS + "login";
    private static final String URL_REGISTER = BASE_SERVER_ADDRESS + "register";
    private static final String URL_UPLOAD_IMAGE = BASE_SERVER_ADDRESS + "uploadImg";
    private static final String URL_GET_SENT_OR_RECEIVED_MSGS = BASE_SERVER_ADDRESS + "getSendOrReceivedMsgs";

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
        MultipartRequest request = new MultipartRequest(url, params, className, resultListenerNG, filePartName, image);
        RequestManagerApi.getInstance().addToRequestQueue(request);
    }

    private static <T> void makeMultipartRequest(final String url, HashMap<Object, Object> params, Class<T> className, ResultListenerNG<T> resultListenerNG, HashMap<String, File> fileHashMaps) {
        MultipartRequest request = new MultipartRequest(url, params, className, resultListenerNG, fileHashMaps);
        RequestManagerApi.getInstance().addToRequestQueue(request);
    }

    public void login(String email, String password, ResultListenerNG<UserResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.EMAIL, email);
        params.put(ApiConstants.PASSWORD, password);
        makeRequest(Request.Method.POST, URL_LOGIN, params, UserResponse.class, resultListenerNG);
    }

    public void signUp(String name, String email, String password, String mobile, ResultListenerNG<UserResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.NAME, name);
        params.put(ApiConstants.EMAIL, email);
        params.put(ApiConstants.PASSWORD, password);
        params.put(ApiConstants.CONTACT, mobile);
        makeRequest(Request.Method.POST, URL_REGISTER, params, UserResponse.class, resultListenerNG);
    }

    public void uploadImage(File file, String user_id, ResultListenerNG<MediaResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.FILE, file);
        params.put(ApiConstants.USER_ID, user_id);
        params.put(ApiConstants.TYPE, Constants.IMAGE);
        makeMultipartRequest(URL_UPLOAD_IMAGE, params, MediaResponse.class, resultListenerNG, ApiConstants.FILE, file);
    }

    public void updateProfile(String name, String mobile, ResultListenerNG<UserResponse> resultListenerNG) {

    }

    public void getSendOrReceivedMsgs(String type, int offset, ResultListenerNG<SendOrReceivedMessageResponse> listenerNG) {
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        String url = String.format(Locale.ENGLISH, URL_GET_SENT_OR_RECEIVED_MSGS + "?userId=%s&type=%s&offset=%d", userId, type, offset);
        makeRequest(Request.Method.GET, URL_GET_SENT_OR_RECEIVED_MSGS, null, SendOrReceivedMessageResponse.class, listenerNG);
    }
}