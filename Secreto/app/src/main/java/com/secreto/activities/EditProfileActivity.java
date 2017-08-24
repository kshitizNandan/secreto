package com.secreto.activities;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.base_activities.ImagePickerActivity;
import com.secreto.common.Common;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.CustomProgressDialog;
import com.secreto.utils.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends ImagePickerActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    @BindView(R.id.tvChangePass)
    TextView tvChangePass;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    CustomProgressDialog progressDialog;
    EditProfileActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        progressDialog = new CustomProgressDialog(this);
        mActivity = this;
    }


    @Override
    protected void onImageSet(File photoFile) {

    }

    @Override
    protected CropAspectRatio getCropAspectRatio() {
        return null;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_edit_profile;
    }

    @OnClick(R.id.tvChangePass)
    void changePassDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.change_password_dialog, null);
        final EditText et_oldPass = (EditText) contentView.findViewById(R.id.et_oldPass);
        final EditText et_newPass = (EditText) contentView.findViewById(R.id.et_newPass);
        final EditText et_confirmPass = (EditText) contentView.findViewById(R.id.et_confirmPass);
        final TextInputLayout input_layout_oldPass = (TextInputLayout) contentView.findViewById(R.id.input_layout_oldPass);
        final TextInputLayout input_layout_newPass = (TextInputLayout) contentView.findViewById(R.id.input_layout_newPass);
        final TextInputLayout input_layout_confirmPass = (TextInputLayout) contentView.findViewById(R.id.input_layout_confirmPass);
        final Dialog dialog = new Dialog(this, R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(contentView);
        if (dialog.getWindow() != null) {
            dialog.setCancelable(true);
            dialog.show();

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_ok:
                            String oldPass = et_oldPass.getText().toString().trim();
                            String newPass = et_newPass.getText().toString().trim();
                            String confirmPass = et_confirmPass.getText().toString().trim();
                            if (TextUtils.isEmpty(oldPass)) {
                                input_layout_oldPass.setError(getString(R.string.please_enter_your_password));
                            } else if (TextUtils.isEmpty(newPass)) {
                                input_layout_newPass.setError(getString(R.string.please_enter_new_password));
                            } else if (TextUtils.isEmpty(confirmPass)) {
                                input_layout_confirmPass.setError(getString(R.string.please_confirm_your_password));
                            } else if (!newPass.equalsIgnoreCase(confirmPass)) {
                                input_layout_confirmPass.setError(getString(R.string.pass_confirm_pass_validation));
                            } else {
                                //   changePasswordApiCall(oldPass, newPass, dialog);
                            }
                            break;
                        case R.id.iv_close:
                        case R.id.btn_cancel:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            contentView.findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
            contentView.findViewById(R.id.btn_ok).setOnClickListener(onClickListener);
            contentView.findViewById(R.id.iv_close).setOnClickListener(onClickListener);

            class GenericTextWatcher extends TextWatcherMediator {
                private GenericTextWatcher(View view) {
                    super(view);
                }

                @Override
                public void onTextChanged(CharSequence s, View view) {
                    switch (view.getId()) {
                        case R.id.et_oldPass:
                            input_layout_oldPass.setError("");
                            break;
                        case R.id.et_newPass:
                            input_layout_newPass.setError("");
                            break;
                        case R.id.et_confirmPass:
                            input_layout_confirmPass.setError("");
                            break;

                    }
                }
            }
            et_oldPass.addTextChangedListener(new GenericTextWatcher(et_oldPass));
            et_newPass.addTextChangedListener(new GenericTextWatcher(et_newPass));
            et_confirmPass.addTextChangedListener(new GenericTextWatcher(et_confirmPass));
        }
    }

    @OnClick(R.id.tvUpdate)
    void updateProfile() {
        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.name_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, R.string.mobile_phone_number_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (mobile.length() < 10) {
            Toast.makeText(this, R.string.mobile_phone_number_should_be_of_10_digits, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                DataManager.getInstance().updateProfile(name, mobile, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        Logger.d(TAG, "Profile update onSuccess : " + response);
                        progressDialog.dismiss();
                        /*if (photoFile != null && photoFile.exists()) {
                            uploadImageApiCall(response.getUser());
                        } else {
                            showSuccessDialog(response.getMessage(), response.getUser());
                        }*/
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Logger.e(TAG, "signUp error : " + error.getMessage());
                            Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "signUp error : " + baseResponse.getMessage());
                            Toast.makeText(mActivity, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.edit_profile);
    }

    @Override
    public boolean isShowHomeButton() {
        return true;
    }

    @Override
    protected void onBackPress() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_right_animation);
    }
}
