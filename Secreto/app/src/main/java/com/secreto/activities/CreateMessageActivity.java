package com.secreto.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.secreto.common.Constants;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.CustomProgressDialog;
import com.secreto.utils.Logger;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateMessageActivity extends BaseActivityWithActionBar {
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.etMessage)
    EditText etMessage;
    private CustomProgressDialog progressDialog;
    private String userId;
    private Activity mActivity;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initView();
    }

    private void init() {
        mActivity = this;
        progressDialog = new CustomProgressDialog(this);
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
        User user = (User) getIntent().getSerializableExtra(Constants.USER);
        if (user != null) {
            this.userId = user.getUserId();
            tvUserName.setText(user.getName());
            iv_profileImg.setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
        }
    }

    private void initView() {
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_create_message;
    }


    @OnClick(R.id.tvSend)
    void sendMessage() {
        String message = etMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.message_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                String messageClue = "";
                DataManager.getInstance().sendMessage(userId, message, messageClue, new ResultListenerNG<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        progressDialog.hide();
                        Common.showAlertDialog(mActivity, response.getMessage(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                onBackPress();
                            }
                        });
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

    @Override
    protected void onBackPress() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_bottom);
    }
}
