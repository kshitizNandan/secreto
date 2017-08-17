package com.secreto.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithTransparentActionBar;
import com.secreto.common.Common;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.fonts.SpannableTextView;
import com.secreto.fonts.TermsAndPrivacyClickedListener;
import com.secreto.model.StatusMessage;
import com.secreto.model.User;
import com.secreto.model.UserResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.LoginLogoutHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivityWithTransparentActionBar {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.etFirstName)
    EditText etFirstName;
    @BindView(R.id.etLastName)
    EditText etLastName;
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
    private ProgressDialog progressDialog;
    private AlertDialog passwordInfoDialog;
    private AlertDialog registrationSuccessDialog;

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
        init();
        initView();
    }

    private void init() {
    }

    private void initView() {
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

    @OnClick(R.id.tvCreateAccount)
    void onClickCreateAccount() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String mobile = etMobile.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, R.string.first_name_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!Common.isValidName(firstName)) {
            Toast.makeText(this, R.string.entered_first_name_is_invalid, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, R.string.last_name_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!Common.isValidName(lastName)) {
            Toast.makeText(this, R.string.entered_last_name_is_invalid, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.email_id_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!Common.isValidEmail(email)) {
            Toast.makeText(this, R.string.invalid_email_id_format, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, R.string.mobile_phone_number_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!Common.isValidMobile(mobile)) {
            Toast.makeText(this, R.string.mobile_phone_number_should_be_of_10_digits, Toast.LENGTH_SHORT).show();
        } else if (!cbTermsOfUse.isChecked()) {
            Toast.makeText(this, R.string.please_agree_to_terms_of_use, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();

                DataManager.getInstance().signUp(firstName, lastName, email, password, mobile, new ResultListenerNG<StatusMessage>() {
                    @Override
                    public void onSuccess(StatusMessage response) {
                        Logger.d(TAG, "signUp onSuccess : " + response);
                        progressDialog.dismiss();
                        showSuccessDialog(response.getMessage());
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        StatusMessage statusMessage = Common.getStatusMessage(error);
                        if (statusMessage == null || TextUtils.isEmpty(statusMessage.getMessage())) {
                            Logger.e(TAG, "signUp error : " + error.getMessage());
                            Toast.makeText(SignUpActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "signUp error : " + statusMessage.getMessage());
                            Toast.makeText(SignUpActivity.this, statusMessage.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        if (passwordInfoDialog != null && passwordInfoDialog.isShowing())
            passwordInfoDialog.dismiss();
        if (registrationSuccessDialog != null && registrationSuccessDialog.isShowing())
            registrationSuccessDialog.dismiss();
    }

    private void showSuccessDialog(String message) {
        if (registrationSuccessDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            registrationSuccessDialog.dismiss();
                            finish();
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

    private void passwordCriteriaDialog() {
        if (passwordInfoDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.password_criteria)
                    .setMessage(R.string.password_criteria_desc)
                    .setPositiveButton(R.string.ok, null);
            passwordInfoDialog = builder.create();
        }
        passwordInfoDialog.show();
    }
}

