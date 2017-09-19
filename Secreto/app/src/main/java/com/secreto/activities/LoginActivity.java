package com.secreto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.LoginLogoutHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivityWithActionBar {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.rememberCheckBox)
    CheckBox rememberCheckBox;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.input_layout_email_editText)
    TextInputLayout textInputLayoutEmail;
    @BindView(R.id.input_layout_password_editText)
    TextInputLayout textInputLayoutPassword;

    @Override
    public int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        setTextWatcher();
    }


    private void initView() {
        etEmail.setText(SharedPreferenceManager.getLoginCredentials(SharedPreferenceManager.EMAIL));
        etPassword.setText(SharedPreferenceManager.getLoginCredentials(SharedPreferenceManager.PASS));
        if (!TextUtils.isEmpty(etEmail.getText())) {
            rememberCheckBox.setChecked(true);
        }
    }

    @OnClick(R.id.btnLogin)
    void onClickLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError(getString(R.string.email_id_can_not_be_left_blank));
        } else if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError(getString(R.string.password_can_not_be_left_blank));
        } else {
            if (Common.isOnline(this)) {
                if (rememberCheckBox.isChecked())
                    SharedPreferenceManager.sertLoginCredentials(email, password);
                else SharedPreferenceManager.removeLoginCredentials();

                progressDialog.show();
                progressDialog.setMessage(getString(R.string.signing_in));
                DataManager.getInstance().login(email, password, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        Logger.d(TAG, "login onSuccess : " + response);
                        progressDialog.dismiss();
                        if (response.getUser() != null) {
                            goToHomeScreen(response.getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Logger.e(TAG, "loginActivityApi error : " + error.getMessage());
                            Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "loginActivityApi error : " + baseResponse.getMessage());
                            Toast.makeText(LoginActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTextWatcher() {
        etEmail.addTextChangedListener(new TextWatcherMediator(etEmail) {
            @Override
            public void onTextChanged(CharSequence s, View view) {
                if (!TextUtils.isEmpty(s.toString())) {
                    textInputLayoutEmail.setError("");
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcherMediator(etPassword) {
            @Override
            public void onTextChanged(CharSequence s, View view) {
                if (!TextUtils.isEmpty(s.toString())) {
                    textInputLayoutPassword.setError("");
                }
            }
        });
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

