package com.secreto.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.secreto.R;
import com.secreto.activities.HomeActivity;
import com.secreto.common.Constants;
import com.secreto.common.MyApplication;
import com.secreto.common.SharedPreferenceManager;

import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        checkNotifyAppForBackForeground(notification);
    }

    private void checkNotifyAppForBackForeground(RemoteMessage.Notification notification) {
        ActivityManager am = (ActivityManager) MyApplication.getInstance().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClass().getSimpleName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String className = componentInfo.getClassName();
        Log.v("", "class Name-" + className);
        if (componentInfo.getPackageName().equalsIgnoreCase(MyApplication.getInstance().getPackageName())) {
            Log.v("", "inside app-");
            String classNameTag = HomeActivity.class.getName();
            Log.v(TAG, "classNameTag-->" + classNameTag);
            if (className.equalsIgnoreCase(classNameTag)) {
                sendBroadcastMessage();
            } else {
                if (SharedPreferenceManager.getNotificationService()) // weather user turned off the notifications of not
                    showNotification(notification);
            }
        } else {
            if (SharedPreferenceManager.getNotificationService()) // weather user turned off the notifications of not
                showNotification(notification);
        }
        sendBroadcastMessage();
    }


    private void showNotification(RemoteMessage.Notification notification) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher).setTicker(getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(!TextUtils.isEmpty(notification.getBody()) ? notification.getBody() : ""))
                .setContentText(!TextUtils.isEmpty(notification.getBody()) ? notification.getBody() : "");

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    // Send Broadcast to My Events
    private void sendBroadcastMessage() {
        Intent intent = new Intent(Constants.REFRESH_LIST_BROADCAST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}