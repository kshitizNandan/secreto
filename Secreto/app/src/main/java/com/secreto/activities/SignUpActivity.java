package com.secreto.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.ImagePickerActivity;
import com.secreto.common.Common;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.fonts.SpannableTextView;
import com.secreto.fonts.TermsAndPrivacyClickedListener;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.BaseResponse;
import com.secreto.model.User;
import com.secreto.model.UserResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.utils.NetworkImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends ImagePickerActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.cbTermsOfUse)
    CheckBox cbTermsOfUse;
    @BindView(R.id.tvTermsOfUse)
    SpannableTextView tvTermsOfUse;
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    private ProgressDialog progressDialog;
    private AlertDialog registrationSuccessDialog;
    private File photoFile;

    @Override
    public int getLayoutResource() {
        return R.layout.activity_sign_up;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.sign_up);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        tvTermsOfUse.setTermsAndPrivacyClickedListener(new TermsAndPrivacyClickedListener() {
            @Override
            public void onClickTerms(View view) {
                //  WebViewLoginFlowActivity.startActivity(SignUpActivity.this, getString(R.string.terms_of_use), Constants.AppUrls.TERMS_OF_USE);
            }

            @Override
            public void onClickPolicy(View view) {
                //  WebViewLoginFlowActivity.startActivity(SignUpActivity.this, getString(R.string.privacy_policy), Constants.AppUrls.PRIVACY_POLICY);
            }
        });
    }

    @OnClick(R.id.iv_profileImg)
    void onProfileImgClick() {
        showTakeImagePopup();
    }

    @OnClick(R.id.tvCreateAccount)
    void onClickCreateAccount() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String mobile = etMobile.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.name_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.email_id_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!Common.isValidEmail(email)) {
            Toast.makeText(this, R.string.invalid_email_id_format, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, R.string.confirm_password_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.equals(password, confirmPassword)) {
            Toast.makeText(this, R.string.password_and_confirm_password_does_not_match, Toast.LENGTH_SHORT).show();
        } else if (!cbTermsOfUse.isChecked()) {
            Toast.makeText(this, R.string.please_agree_to_terms_of_use, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                DataManager.getInstance().signUp(name, email, password, mobile, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        Logger.d(TAG, "signUp onSuccess : " + response);
                        progressDialog.dismiss();
                        if (photoFile != null && photoFile.exists()) {
                            uploadImageApiCall(response.getUser());
                        } else {
                            showSuccessDialog(response.getMessage(), response.getUser());
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Logger.e(TAG, "signUp error : " + error.getMessage());
                            Toast.makeText(SignUpActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "signUp error : " + baseResponse.getMessage());
                            Toast.makeText(SignUpActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void uploadImageApiCall(final User user) {
        if (Common.isOnline(this)) {
            progressDialog.show();
            DataManager.getInstance().uploadImage(photoFile, user.getUserId(), new ResultListenerNG<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse response) {
                    Logger.d(TAG, "Image Upload onSuccess : " + response);
                    progressDialog.dismiss();
                    showSuccessDialog(getString(R.string.user_successfully_registered), user);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Logger.e(TAG, "Image upload error : " + error.getMessage());
                        Toast.makeText(SignUpActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "Image Upload error : " + baseResponse.getMessage());
                        Toast.makeText(SignUpActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onImageSet(File photoFile) {
        if (photoFile != null && photoFile.exists()) {
            this.photoFile = photoFile;
            iv_profileImg.setImageUrl(photoFile.getAbsolutePath(), ImageCacheManager.getInstance().getImageLoader());
        }
    }

    @Override
    protected CropAspectRatio getCropAspectRatio() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        if (registrationSuccessDialog != null && registrationSuccessDialog.isShowing())
            registrationSuccessDialog.dismiss();
    }

    private void showSuccessDialog(String message, final User user) {
        if (registrationSuccessDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            registrationSuccessDialog.dismiss();
                            goToHomeScreen(user);
                        }
                    });
            registrationSuccessDialog = builder.create();
        }
        registrationSuccessDialog.show();
    }

    private void goToHomeScreen(User user) {
        LoginLogoutHandler.storeUserIntoPrefs(user);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
}

