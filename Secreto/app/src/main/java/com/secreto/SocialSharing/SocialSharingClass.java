package com.secreto.SocialSharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.utils.Logger;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Aashish Tomar on 9/12/2017.
 */

public class SocialSharingClass extends BaseActivityWithActionBar implements View.OnClickListener {
    private static final String TAG = SocialSharingClass.class.getSimpleName();
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String LINKEDIN = "linkedIn";
    private OnItemSelectedItemListener itemSelectedItemListener;
    private AlertDialog alertDialog;

    public SocialSharingClass(Context context, OnItemSelectedItemListener itemSelectedItemListener) {
        this.itemSelectedItemListener = itemSelectedItemListener;
        shareEvent(context);
    }

    private void shareEvent(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.social_sharing_chooser_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = dialogBuilder.show();
        dialogBuilder.setCancelable(false);
        dialogView.findViewById(R.id.tv_facebook).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_twitter).setOnClickListener(this);
        dialogView.findViewById(R.id.tv_linkedin).setOnClickListener(this);
    }

    public static void ShareOnFacebook(Context context, String message, CallbackManager callbackManager, FacebookCallback<Sharer.Result> facebookCallback) {
        try {
            ShareDialog shareDialog = new ShareDialog((Activity) context);
            shareDialog.show(new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(message))
                    .build(), ShareDialog.Mode.AUTOMATIC);
            shareDialog.registerCallback(callbackManager, facebookCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "app not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public static void ShareOnTwitter(Activity context, String message) {
        try {
            TweetComposer.Builder builder = new TweetComposer.Builder(context)
                    .text(context.getString(R.string.app_name) + " " + context.getString(R.string.by))
                    .url(new URL(message));
            builder.show();
        } catch (MalformedURLException e) {
            Logger.v("error creating tweet intent", "" + e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_facebook:
                if (itemSelectedItemListener != null) {
                    itemSelectedItemListener.selectedItem(FACEBOOK);
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
                break;
            case R.id.tv_twitter:
                if (itemSelectedItemListener != null) {
                    itemSelectedItemListener.selectedItem(TWITTER);
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
                break;
            case R.id.tv_linkedin:
                if (itemSelectedItemListener != null) {
                    itemSelectedItemListener.selectedItem(LINKEDIN);
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
                break;
        }
    }

    @Override
    public int getLayoutResource() {
        return 0;
    }

    public interface OnItemSelectedItemListener {
        void selectedItem(String item);
    }
}
