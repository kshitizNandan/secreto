package com.secreto.data.volley;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.secreto.common.MyApplication;


public class RequestManagerApi {
    private static final String TAG = RequestManagerApi.class.getSimpleName();
    private static RequestQueue mRequestQueue;
    private static RequestManagerApi singleton;

    private RequestManagerApi() {
    }

    public static RequestManagerApi getInstance() {
        if (singleton == null) {
            singleton = new RequestManagerApi();
        }
        return singleton;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MyApplication.getInstance());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
