package com.secreto.messageSharing;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;

import com.secreto.R;
import com.secreto.common.Constants;
import com.secreto.utils.SDCardHandler;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Aashish Tomar on 9/19/2017.
 */

public class MessageSharing {


    public static void persistImage(Context context, Bitmap bitmap) {
        File mediaDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mediaDir = new File(SDCardHandler.IMAGE_STORAGE_PATH);
        } else {
            mediaDir = new File(context.getFilesDir(), Constants.APP_NAME);
        }
        if (!mediaDir.exists()) {
            mediaDir.mkdirs();
        }
        File imageFile = new File(mediaDir, System.currentTimeMillis() + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            // Share image
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFile.getAbsolutePath()));
            context.startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // llButtons.setVisibility(View.VISIBLE);
        }
    }
}
