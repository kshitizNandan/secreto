package com.secreto.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateMessageActivity extends BaseActivityWithActionBar {
    private static final String TAG = CreateMessageActivity.class.getSimpleName();
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.etMessage)
    EditText etMessage;
    private ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initView();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
    }

    private void initView() {
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_create_message;
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, CreateMessageActivity.class);
        activity.startActivity(intent);
    }

    @OnClick(R.id.tvSend)
    void sendMessage() {
        String message = etMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.message_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                String userId = "79";
                String messageClue = "";
                DataManager.getInstance().sendMessage(userId, message, messageClue, new ResultListenerNG<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        progressDialog.hide();
                        Logger.d(TAG, "Contact Us onSuccess : " + response);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.hide();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Toast.makeText(CreateMessageActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateMessageActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
