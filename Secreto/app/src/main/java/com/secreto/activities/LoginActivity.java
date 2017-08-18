package com.secreto.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithTransparentActionBar;
import com.secreto.common.Common;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.StatusMessage;
import com.secreto.model.User;
import com.secreto.model.UserResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.LoginLogoutHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity  extends BaseActivityWithTransparentActionBar {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    private ProgressDialog progressDialog;


    @Override
    public int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
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
    }

    @OnClick(R.id.tvLogin)
    void onClickLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.email_id_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else if (!Common.isValidEmail(email)) {
            Toast.makeText(this, R.string.invalid_email_id_format, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                DataManager.getInstance().login(email, password, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        Logger.d(TAG, "login onSuccess : " + response);
                        progressDialog.dismiss();
                        goToHomeScreen(response.getUser());
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        StatusMessage statusMessage = Common.getStatusMessage(error);
                        if (statusMessage == null || TextUtils.isEmpty(statusMessage.getMessage())) {
                            Logger.e(TAG, "loginActivityApi error : " + error.getMessage());
                            Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "loginActivityApi error : " + statusMessage.getMessage());
                            Toast.makeText(LoginActivity.this, statusMessage.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.tvSignUp)
    void onClickSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void goToHomeScreen(User user) {
        LoginLogoutHandler.storeUserIntoPrefs(user);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}

