package com.secreto.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.ImagePickerActivity;
import com.secreto.common.Common;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.MediaResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.Logger;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.widgets.CircleTransform;
import com.secreto.widgets.SpannableTextView;
import com.secreto.widgets.TermsAndPrivacyClickedListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends ImagePickerActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.cbTermsOfUse)
    CheckBox cbTermsOfUse;
    @BindView(R.id.tvTermsOfUse)
    SpannableTextView tvTermsOfUse;
    @BindView(R.id.iv_profileImg)
    ImageView iv_profileImg;
    @BindView(R.id.input_layout_name_editText)
    TextInputLayout textInputLayoutName;
    @BindView(R.id.input_layout_email_editText)
    TextInputLayout textInputLayoutEmail;
    @BindView(R.id.input_layout_userName_editText)
    TextInputLayout textInputLayoutuserName;
    @BindView(R.id.input_layout_password_editText)
    TextInputLayout textInputLayoutPassword;
    @BindView(R.id.input_layout_confirmPass_editText)
    TextInputLayout textInputLayoutconfirmPass;
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
    public int setHomeButtonDrawable() {
        return R.drawable.back_arrow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        setTextWatcher();
        //to remove password 
        etPassword.addTextChangedListener(new TextWatcherMediator(etPassword) {
            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etPassword.setText(result);
                    etPassword.setSelection(result.length());
                }
            }
        });
        etConfirmPassword.addTextChangedListener(new TextWatcherMediator(etConfirmPassword) {
            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etPassword.setText(result);
                    etPassword.setSelection(result.length());
                }
            }
        });
    }

    private void init() {
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

    private void setTextWatcher() {
        class GenericTextWatcher extends TextWatcherMediator {
            private GenericTextWatcher(View view) {
                super(view);
            }

            @Override
            public void onTextChanged(CharSequence s, View view) {
                switch (view.getId()) {
                    case R.id.etName:
                        textInputLayoutName.setError("");
                        break;
                    case R.id.etUserName:
                        textInputLayoutuserName.setError("");
                        break;
                    case R.id.etEmail:
                        textInputLayoutEmail.setError("");
                        break;
                    case R.id.etPassword:
                        textInputLayoutPassword.setError("");
                        break;
                    case R.id.etConfirmPassword:
                        textInputLayoutconfirmPass.setError("");
                        break;
                }
            }
        }
        etName.addTextChangedListener(new GenericTextWatcher(etName));
        etUserName.addTextChangedListener(new GenericTextWatcher(etUserName));
        etEmail.addTextChangedListener(new GenericTextWatcher(etEmail));
        etPassword.addTextChangedListener(new GenericTextWatcher(etPassword));
        etConfirmPassword.addTextChangedListener(new GenericTextWatcher(etConfirmPassword));
    }

    @OnClick(R.id.tv_status)
    void changeStatusPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.change_status_dialog, null);
        final EditText et_status = (EditText) contentView.findViewById(R.id.et_status);
        final TextInputLayout input_layout_status = (TextInputLayout) contentView.findViewById(R.id.input_layout_status);
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
                            String status = et_status.getText().toString().trim();
                            if (TextUtils.isEmpty(status)) {
                                input_layout_status.setError(getString(R.string.please_enter_cool_status));
                            } else {
                                dialog.dismiss();
                                tv_status.setText(status);
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

            et_status.addTextChangedListener(new TextWatcherMediator(et_status) {
                @Override
                public void onTextChanged(CharSequence s, View view) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        input_layout_status.setError("");
                    }
                }
            });
        }
    }

    @OnClick(R.id.iv_profileImg)
    void onProfileImgClick() {
        showTakeImagePopup();
    }

    @OnClick(R.id.btnCreateAccount)
    void onClickCreateAccount() {
        String name = etName.getText().toString();
        String userName = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String status = tv_status.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            textInputLayoutName.setError(getString(R.string.nick_name_can_not_be_left_blank));
        } else if (TextUtils.isEmpty(userName)) {
            textInputLayoutuserName.setError(getString(R.string.user_name_can_not_be_left_blank));
        } else if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError(getString(R.string.email_id_can_not_be_left_blank));
        } else if (!Common.isValidEmail(email)) {
            textInputLayoutEmail.setError(getString(R.string.invalid_email_id_format));
        } else if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError(getString(R.string.password_can_not_be_left_blank));
        } else if (TextUtils.isEmpty(confirmPassword)) {
            textInputLayoutconfirmPass.setError(getString(R.string.confirm_password_can_not_be_left_blank));
        } else if (!TextUtils.equals(password, confirmPassword)) {
            textInputLayoutconfirmPass.setError(getString(R.string.password_and_confirm_password_does_not_match));
        } else if (!cbTermsOfUse.isChecked()) {
            Toast.makeText(this, R.string.please_agree_to_terms_of_use, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.setMessage(getString(R.string.signing_up));
                progressDialog.show();
                DataManager.getInstance().signUp(name, userName, email, password, "", status, new ResultListenerNG<UserResponse>() {
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
            DataManager.getInstance().uploadImage(photoFile, user.getUserId(), new ResultListenerNG<MediaResponse>() {
                @Override
                public void onSuccess(MediaResponse response) {
                    Logger.d(TAG, "Image Upload onSuccess : " + response);
                    progressDialog.dismiss();
                    if (!TextUtils.isEmpty(response.getMedia())) {
                        user.setProfile_pic(response.getMedia());
                        SharedPreferenceManager.setUserObject(user);
                    }
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
            int size = Common.dipToPixel(this, 80);
            Picasso.with(this).load(photoFile).transform(new CircleTransform()).resize(size, size).into(iv_profileImg);
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
        Common.hideKeyboard(this, etConfirmPassword);
    }

}

