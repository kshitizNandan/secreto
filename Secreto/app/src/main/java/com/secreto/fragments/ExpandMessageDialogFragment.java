package com.secreto.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.DateFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import static com.secreto.activities.HomeActivity.RC_SEND_MESSAGE;

public class ExpandMessageDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ExpandMessageDialogFragment.class.getSimpleName();
    private TextView tv_message;
    private TextView tv_time;
    private TextView tv_clue;
    private ImageButton ivReply, imgDelete, imgShare;
    private ProgressDialog progressDialog;
    private MessageAndUserResponse response;
    private View viewReply;
    private AlertDialog deleteDialog;
    private Dialog dialog;
    private ImageView img;

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
        imgShare.setOnClickListener(this);
    }

    private void initViews(Dialog dialog) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        tv_time = (TextView) dialog.findViewById(R.id.tv_time);
        tv_clue = (TextView) dialog.findViewById(R.id.tvClue);
        ivReply = (ImageButton) dialog.findViewById(R.id.imgReply);
        viewReply = dialog.findViewById(R.id.viewReply);
        imgDelete = (ImageButton) dialog.findViewById(R.id.imgDelete);
        imgShare = (ImageButton) dialog.findViewById(R.id.imgShare);
        img = (ImageView) dialog.findViewById(R.id.img);
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
                tv_clue.setVisibility(View.VISIBLE);
                tv_clue.setText(message.getMessageClue());
            } else {
                tv_clue.setVisibility(View.GONE);
            }
            if (message.getCanReply().equalsIgnoreCase("YES")) {
                ivReply.setVisibility(View.VISIBLE);
                viewReply.setVisibility(View.VISIBLE);
            } else {
                ivReply.setVisibility(View.GONE);
                viewReply.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgReply:
                if (response != null && response.getUser() != null) {
                    CreateMessageActivity.startActivityForResult(getActivity(), response.getUser(), TAG, RC_SEND_MESSAGE);
                    getActivity().overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                }
                break;
            case R.id.tvClue:
                if (response != null && response.getUser() != null) {
                    ProfileActivity.startActivity(getActivity(), response.getUser());
                    getActivity().overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                }
                break;
            case R.id.imgDelete:
                if (response.getMessage() != null) {
                    if (deleteDialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.app_name))
                                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        deleteMessage();
                                    }

                                })
                                .setNegativeButton(getString(R.string.no), null);
                        deleteDialog = builder.create();
                    }
                    deleteDialog.show();
                }
                break;
            case R.id.imgShare:
                RelativeLayout rlMain = (RelativeLayout) dialog.findViewById(R.id.rlMain);
                rlMain.setDrawingCacheEnabled(true);
                rlMain.buildDrawingCache();
                Bitmap bm = rlMain.getDrawingCache();
                //  img.setImageBitmap(bm);
                File imgFile = persistImage(bm, getString(R.string.image));
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(imgFile.getAbsolutePath());
                img.setImageURI(screenshotUri);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                break;
        }
    }

    private void deleteMessage() {
        if (Common.isOnline(getActivity())) {
            progressDialog.show();
            DataManager.getInstance().deleteMessageApiCall(response.getMessage().getMessageId(), response.getMessageType(), new ResultListenerNG<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse response) {
                    progressDialog.dismiss();
                    if (!TextUtils.isEmpty(response.getMessage())) {
                        Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    sendBroadcastMessage();
                    dismiss();
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse statusMessage = Common.getStatusMessage(error);
                    if (statusMessage == null || TextUtils.isEmpty(statusMessage.getMessage())) {
                        Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), statusMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    // Send Broadcast to My Events
    private void sendBroadcastMessage() {
        Intent intent = new Intent(Constants.REFRESH_LIST_BROADCAST);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = getActivity().getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
        }
        return imageFile;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
