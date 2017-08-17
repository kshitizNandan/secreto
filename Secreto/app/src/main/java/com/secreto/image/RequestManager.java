package com.secreto.image;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;

/**
 * Manager for the queue
 * 
 * @author Trey Robinson
 *
 */
public class RequestManager {
	
	/**
	 * the queue :-)
	 */
	private static RequestQueue mRequestQueue;

	/**
	 * Nothing to see here.
	 */
	private RequestManager() {
	 // no instances
	} 

	/**
	 * @param context $application context
	 */
	public static void init(Context context) {
		mRequestQueue = SuperVolley.newRequestQueue(context);
	}
	public static void init(Context context, HttpStack stack) {
		mRequestQueue = SuperVolley.newRequestQueue(context, stack);
	}

	/**
	 * @return
	 * 		instance of the queue
	 * @throws "IllegalStatException" if init has not yet been called
	 */
	public static RequestQueue getRequestQueue() {
	    if (mRequestQueue != null) {
	        return mRequestQueue;
	    } else {
	        throw new IllegalStateException("Not initialized");
	    }
	}
}
