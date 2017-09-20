package com.secreto.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.activities.CreateMessageActivity;
import com.secreto.activities.ProfileActivity;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.common.MyApplication;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.Message;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.DateFormatter;
import com.secreto.utils.SDCardHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import static com.secreto.activities.HomeActivity.RC_SEND_MESSAGE;

public class ExpandMessageDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ExpandMessageDialogFragment.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_ALL = 500;
    private TextView tv_message;
    private TextView tv_time;
    private TextView tv_clue;
    private ImageView ivReply, imgDelete, ivShare;
    private ProgressDialog progressDialog;
    private MessageAndUserResponse response;
    private View viewReply;
    private AlertDialog deleteDialog;
    private Dialog dialog;
    private LinearLayout llButtons;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            response = (MessageAndUserResponse) getArguments().getSerializable(Constants.MESSAGE_AND_USER_RESPONSE);
        }
        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Point point = MyApplication.getScreenSize();
        final int width = point.x;
        final int height = point.y / 2;
        dialog.setContentView(R.layout.fragment_expand_message);
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        initViews(dialog);
        if (response != null) {
            setListeners();
            setViews();
        }
        return dialog;
    }

    private void setListeners() {
        tv_clue.setOnClickListener(this);
        ivReply.setOnClickListener(this);
        imgDelete.setOnClickListener(this);
        ivShare.setOnClickListener(this);
    }

    private void initViews(Dialog dialog) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        tv_time = (TextView) dialog.findViewById(R.id.tv_time);
        tv_clue = (TextView) dialog.findViewById(R.id.tvClue);
        ivReply = (ImageView) dialog.findViewById(R.id.ivReply);
        viewReply = dialog.findViewById(R.id.viewReply);
        imgDelete = (ImageView) dialog.findViewById(R.id.ivDelete);
        ivShare = (ImageView) dialog.findViewById(R.id.ivShare);
        llButtons = (LinearLayout) dialog.findViewById(R.id.llButtons);
    }

    private void setViews() {
        Message message = response.getMessage();
        tv_message.setText(message.getMessage());
        tv_time.setText(DateFormatter.getTimeString(message.getCreatedDate()));
        ivReply.setTag(response.getUser());
        tv_clue.setTag(response.getUser());
        if (response.getMessageType().equalsIgnoreCase(Constants.SENT)) {
            ivReply.setVisibility(View.GONE);
            viewReply.setVisibility(View.GONE);
            if (response.getUser() != null && !TextUtils.isEmpty(response.getUser().getName()))
                tv_clue.setText(String.format(Locale.ENGLISH, getString(R.string.to_x), response.getUser().getName()));
        } else {
            if (!TextUtils.isEmpty(message.getMessageClue())) {
                tv_clue.setText(message.getMessageClue());
            } else {
                tv_clue.setText(getString(R.string.anonymous_sender));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivReply:

                break;
            case R.id.tvClue:
                if (response != null && response.getUser() != null) {
                    ProfileActivity.startActivity(getActivity(), response.getUser());
                    getActivity().overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                    dismiss();
                }
                break;
            case R.id.ivDelete:

                break;
            case R.id.ivShare:
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                break;
        }
    }


    // Send Broadcast to My Events
    private void sendBroadcastMessage() {
        Intent intent = new Intent(Constants.REFRESH_LIST_BROADCAST);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }


    private void persistImage() {
        RelativeLayout rlMain = (RelativeLayout) dialog.findViewById(R.id.rlMain);
        rlMain.setDrawingCacheEnabled(true);
        rlMain.buildDrawingCache();
        Bitmap bitmap = rlMain.getDrawingCache();

        String pathofBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, getString(R.string.app_name), null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent intent1 = new Intent(android.content.Intent.ACTION_SEND);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent1.setType("image/*");
        startActivity(Intent.createChooser(intent1, getString(R.string.share_message_using)));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isPermissionGranted = true;
        switch (requestCode) {
            case PERMISSION_ALL:
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false;
                        break;
                    }
                }
                break;
        }
        if (isPermissionGranted) {
            persistImage();
        } else {
            Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }


}
