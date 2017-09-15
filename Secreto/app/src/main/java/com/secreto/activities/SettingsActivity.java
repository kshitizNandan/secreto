package com.secreto.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.widgets.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivityWithActionBar {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    @BindView(R.id.llHeaderView)
    LinearLayout llHeaderView;
    @BindView(R.id.toggleButton)
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        toggleButton.setChecked(SharedPreferenceManager.getNotificationService());
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Common.showAlertDialog(SettingsActivity.this, getString(R.string.off_notification_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferenceManager.setNotificationService(false);
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SharedPreferenceManager.setNotificationService(true);
                            toggleButton.setChecked(true);
                        }
                    });
                } else {
                    SharedPreferenceManager.setNotificationService(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetUserData();
    }

    private void resetUserData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            ((TextView) llHeaderView.findViewById(R.id.tv_name)).setText(user.getName());
            ((TextView) llHeaderView.findViewById(R.id.tv_status)).setText(user.getCaption());
            ImageView iv_profileImg = (ImageView) llHeaderView.findViewById(R.id.iv_profileImg);
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(this, 80);
                Picasso.with(this).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_user));
            }
        }
    }

    @OnClick(R.id.llHeaderView)
    void headerClick() {
        ProfileActivity.startActivity(SettingsActivity.this, SharedPreferenceManager.getUserObject());
        overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
    }

    @OnClick(R.id.tvEditProfile)
    void editProfile() {
        EditProfileActivity.startActivity(this);
        overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
    }

    @OnClick(R.id.tvShare)
    void shareProfile() {
        Common.shareProfile(this);
    }


    @OnClick(R.id.tvLogout)
    void logout() {
        LoginLogoutHandler.logoutUserWithConfirm(this);
    }

    @OnClick(R.id.tvFeedback)
    void sendFeedback() {
        showFeedbackDialog();
    }

    private void showFeedbackDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.feedback_dialog, null);
        final EditText etFeedback = (EditText) contentView.findViewById(R.id.etFeedback);
        final TextInputLayout input_layout_feedback = (TextInputLayout) contentView.findViewById(R.id.input_layout_feedback);
        final Dialog dialog = new Dialog(this, R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(contentView);
        if (dialog.getWindow() != null) {
            dialog.setCancelable(false);
            dialog.show();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btnOk:
                            String feedback = etFeedback.getText().toString().trim();
                            if (TextUtils.isEmpty(feedback)) {
                                input_layout_feedback.setError(getString(R.string.please_provide_your_valuable_feedback));
                            } else {
                                feedbackApiCall(feedback, dialog);
                            }
                            break;
                        case R.id.ivClose:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            contentView.findViewById(R.id.btnOk).setOnClickListener(onClickListener);
            contentView.findViewById(R.id.ivClose).setOnClickListener(onClickListener);
            etFeedback.addTextChangedListener(new TextWatcherMediator(etFeedback) {
                @Override
                public void onTextChanged(CharSequence s, View view) {
                    if (!TextUtils.isEmpty(s)) {
                        input_layout_feedback.setError("");
                    }
                }
            });
        }
    }

    private void feedbackApiCall(String feedback, final Dialog parentDialog) {
        if (Common.isOnline(this)) {
            DataManager.getInstance().feedbackApiCall(feedback, new ResultListenerNG<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse response) {
                    parentDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Logger.e(TAG, "Delete Account error : " + error.getMessage());
                        Toast.makeText(SettingsActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "Delete Account error : " + baseResponse.getMessage());
                        Toast.makeText(SettingsActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.settings);
    }

    @Override
    protected void onBackPress() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_bottom);
    }
}
