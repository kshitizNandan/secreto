package com.secreto.data;

import android.text.TextUtils;

import com.android.volley.Request;
import com.secreto.common.ApiConstants;
import com.secreto.common.Constants;
import com.secreto.common.MyApplication;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.volley.MultipartRequest;
import com.secreto.data.volley.RequestManagerApi;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.responsemodel.AllUserResponse;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.MediaResponse;
import com.secreto.responsemodel.SendOrReceivedMessageResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.QueryBuilder;

import java.io.File;
import java.util.HashMap;

import static com.secreto.BuildConfig.BASE_SERVER_ADDRESS;

public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private static final String URL_LOGIN = BASE_SERVER_ADDRESS + "login";
    private static final String URL_REGISTER = BASE_SERVER_ADDRESS + "register";
    private static final String URL_UPLOAD_IMAGE = BASE_SERVER_ADDRESS + "uploadImg";
    private static final String SEND_MESSAGE = BASE_SERVER_ADDRESS + "sendMessage";

    private static final String URL_GET_SENT_OR_RECEIVED_MSGS = BASE_SERVER_ADDRESS + "getSendOrReceivedMsgs";
    private static final String URL_FIND_USER = BASE_SERVER_ADDRESS + "findUser";
    private static final String URL_UPDATE_PROFILE = BASE_SERVER_ADDRESS + "updateProfile";
    private static final String URL_GET_ALL_USERS = BASE_SERVER_ADDRESS + "getAllUsers";
    private static final String URL_CHANGE_PASS = BASE_SERVER_ADDRESS + "changePassword";
    private static final String URL_LOGOUT = BASE_SERVER_ADDRESS + "logout";
    private static final String URL_DELETE_MESSAGE = BASE_SERVER_ADDRESS + "deleteMessage";
    private static final String URL_DELETE_ACCOUNT = BASE_SERVER_ADDRESS + "deleteAccount";


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
        params.put(ApiConstants.DEVICE_TOKEN, SharedPreferenceManager.getFcmToken());
        makeRequest(Request.Method.POST, URL_LOGIN, params, UserResponse.class, resultListenerNG);
    }

    public void signUp(String name, String userName, String email, String password, String mobile, String status, ResultListenerNG<UserResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.NAME, name);
        params.put(ApiConstants.USER_NAME, userName);
        params.put(ApiConstants.EMAIL, email);
        params.put(ApiConstants.PASSWORD, password);
        params.put(ApiConstants.CONTACT, mobile);
        params.put(ApiConstants.CAPTION, !TextUtils.isEmpty(status) ? status.equalsIgnoreCase("status") ? "Available" : status : "Available");
        params.put(ApiConstants.DEVICE_TOKEN, SharedPreferenceManager.getFcmToken());
        makeRequest(Request.Method.POST, URL_REGISTER, params, UserResponse.class, resultListenerNG);
    }

    public void uploadImage(File file, String user_id, ResultListenerNG<MediaResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.FILE, file);
        params.put(ApiConstants.USER_ID, user_id);
        params.put(ApiConstants.TYPE, Constants.IMAGE);
        makeMultipartRequest(URL_UPLOAD_IMAGE, params, MediaResponse.class, resultListenerNG, ApiConstants.FILE, file);
    }

    public void sendMessage(String toUserId, String message, String messageClue, String canReply, ResultListenerNG<BaseResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        params.put(ApiConstants.USER_ID, userId);
        params.put(ApiConstants.MESSAGE, message);
        params.put(ApiConstants.MESSAGE_CLUE, messageClue);
        params.put(ApiConstants.TO_USER_ID, toUserId);
        params.put(ApiConstants.CAN_REPLY, canReply);
        Logger.v(TAG, SEND_MESSAGE);
        makeRequest(Request.Method.POST, SEND_MESSAGE, params, BaseResponse.class, resultListenerNG);
    }

    public void updateProfile(String name, String mobile, String gender, String status, ResultListenerNG<UserResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        params.put(ApiConstants.NAME, name);
        params.put(ApiConstants.CONTACT, mobile);
        params.put(ApiConstants.GENDER, gender);
        params.put(ApiConstants.CAPTION, !TextUtils.isEmpty(status) ? status.equalsIgnoreCase("status") ? "Available" : status : "Available");
        params.put(ApiConstants.USER_ID, userId);
        Logger.v(TAG, URL_UPDATE_PROFILE);
        makeRequest(Request.Method.POST, URL_UPDATE_PROFILE, params, UserResponse.class, resultListenerNG);
    }

    public void getSendOrReceivedMsgs(String type, int offset, ResultListenerNG<SendOrReceivedMessageResponse> listenerNG) {
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.USER_ID, userId);
        params.put(ApiConstants.OFFSET, offset);
        params.put(ApiConstants.TYPE, type);
        String query = QueryBuilder.buildQuery(URL_GET_SENT_OR_RECEIVED_MSGS, params);
        Logger.v(TAG, query);
        makeRequest(Request.Method.GET, query, null, SendOrReceivedMessageResponse.class, listenerNG);
    }

    public void findUser(String userName, ResultListenerNG<UserResponse> resultListenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.USER_NAME, userName);
        makeRequest(Request.Method.POST, URL_FIND_USER, params, UserResponse.class, resultListenerNG);
    }

    public void getAllUsers(String keyword, int offset, ResultListenerNG<AllUserResponse> listenerNG) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.KEYWORD, keyword);
        params.put(ApiConstants.OFFSET, offset);
        String query = QueryBuilder.buildQuery(URL_GET_ALL_USERS, params);
        Logger.v(TAG, query);
        makeRequest(Request.Method.GET, query, null, AllUserResponse.class, listenerNG);
    }

    public void changePassword(String currentPassword, String newPassword, ResultListenerNG<UserResponse> resultListenerNG) {
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.CURRENT_PASS, currentPassword);
        params.put(ApiConstants.NEW_PASS, newPassword);
        params.put(ApiConstants.USER_ID, userId);
        makeRequest(Request.Method.POST, URL_CHANGE_PASS, params, UserResponse.class, resultListenerNG);
    }


    public void logoutApiCall(ResultListenerNG<BaseResponse> resultListenerNG) {
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.DEVICE_TOKEN, SharedPreferenceManager.getFcmToken());
        params.put(ApiConstants.USER_ID, userId);
        makeRequest(Request.Method.POST, URL_LOGOUT, params, BaseResponse.class, resultListenerNG);
    }

    public void deleteMessageApiCall(String messageId, String type, ResultListenerNG<BaseResponse> resultListenerNG) {
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.MESSAGE_ID, messageId);
        params.put(ApiConstants.USER_ID, userId);
        params.put(ApiConstants.TYPE, type);
        makeRequest(Request.Method.POST, URL_DELETE_MESSAGE, params, BaseResponse.class, resultListenerNG);
    }

    public void deleteAccountApiCall(String feedback, ResultListenerNG<BaseResponse> resultListenerNG) {
        String userId = SharedPreferenceManager.getUserObject().getUserId();
        HashMap<Object, Object> params = new HashMap<>();
        params.put(ApiConstants.FEEDBACK, feedback);
        params.put(ApiConstants.USER_ID, userId);
        makeRequest(Request.Method.POST, URL_DELETE_ACCOUNT, params, BaseResponse.class, resultListenerNG);
    }
}