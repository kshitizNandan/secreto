package com.secreto.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.activities.CreateMessageActivity;
import com.secreto.activities.ProfileActivity;
import com.secreto.common.Constants;
import com.secreto.common.MyApplication;
import com.secreto.model.Message;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.model.User;
import com.secreto.utils.DateFormatter;

import java.util.Locale;

import butterknife.BindView;

import static com.secreto.activities.HomeActivity.RC_SEND_MESSAGE;

public class ExpandMessageDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ExpandMessageDialogFragment.class.getSimpleName();
    private TextView tv_message;
    private TextView tv_time;
    private TextView tv_clue;
    private ImageView img_reply;
    private ProgressDialog progressDialog;
    private MessageAndUserResponse response;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            response = (MessageAndUserResponse) getArguments().getSerializable(Constants.MESSAGE_AND_USER_RESPONSE);
        }
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Point point = MyApplication.getScreenSize();
        final int width = point.x;
        dialog.setContentView(R.layout.fragment_expand_message);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
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
        img_reply.setOnClickListener(this);
    }

    private void initViews(Dialog dialog) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        tv_message = (TextView) dialog.findViewById(R.id.tv_message);
        tv_time = (TextView) dialog.findViewById(R.id.tv_time);
        tv_clue = (TextView) dialog.findViewById(R.id.tvClue);
        img_reply = (ImageView) dialog.findViewById(R.id.imgReply);
    }

    private void setViews() {
        Message message = response.getMessage();
        tv_message.setText(message.getMessage());
        tv_time.setText(DateFormatter.getTimeString(message.getCreatedDate()));
        img_reply.setTag(response.getUser());
        tv_clue.setTag(response.getUser());
        if (response.getMessageType().equalsIgnoreCase(Constants.SENT)) {
            img_reply.setVisibility(View.GONE);
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
                img_reply.setVisibility(View.VISIBLE);
            } else {
                img_reply.setVisibility(View.GONE);
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
