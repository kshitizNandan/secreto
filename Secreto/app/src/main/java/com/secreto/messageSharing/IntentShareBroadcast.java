package com.secreto.messageSharing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.secreto.utils.Logger;

/**
 * Created by Aashish Tomar on 9/19/2017.
 */

public class IntentShareBroadcast extends BroadcastReceiver {
    private static final String TAG = IntentShareBroadcast.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String log = "Action: " + intent.getAction() + "\n" +
                "URI: " + intent.toUri(Intent.URI_INTENT_SCHEME) + "\n";
        Logger.d(TAG, log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }
}
