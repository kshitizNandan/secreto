package com.secreto.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
import com.squareup.picasso.Picasso;

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
    @BindView(R.id.btnUpdateAccount)
    TextView btnUpdateAccount;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.iv_profileImg)
    ImageView iv_profileImg;
    @BindView(R.id.input_layout_name_editText)
    TextInputLayout textInputLayoutName;
    @BindView(R.id.input_layout_userName_editText)
    TextInputLayout textInputLayoutuserName;
    @BindView(R.id.etGender)
    EditText etGender;
    @BindView(R.id.tv_status)
    TextView tv_status;
    private File photoFile;
    private int genderSelection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        InitializeData();
        setTextWatcher();
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.edit_profile);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_edit_profile;
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        activity.startActivity(intent);
    }

    private void InitializeData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            etName.setText(user.getName());
            String[] genders = getResources().getStringArray(R.array.gender_types_enum);
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equalsIgnoreCase(user.getGender())) {
                    genderSelection = i;
                    etGender.setText(genders[i]);
                    break;
                }
            }
            etUserName.setText(user.getUserName());
            etEmail.setText(user.getEmail());
            tv_status.setText(user.getCaption());
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(this, 80);
                Picasso.with(this).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_user));
            }
        }
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


                }
            }
        }
        etName.addTextChangedListener(new GenericTextWatcher(etName));
        etUserName.addTextChangedListener(new GenericTextWatcher(etUserName));
        etEmail.addTextChangedListener(new GenericTextWatcher(etEmail));

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


    @OnClick(R.id.etGender)
    void genderSelctionDialog() {
        final CharSequence[] charSequences = getResources().getStringArray(R.array.gender_types_enum);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_gender)).
                        setSingleChoiceItems(charSequences, genderSelection, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, final int selectedItem) {
                                genderSelection = selectedItem;
                                etGender.setText(charSequences[selectedItem]);
                                dialog.dismiss();
                            }

                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialogBuilder.show();
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
            dialog.setCancelable(false);
            dialog.show();
            et_oldPass.addTextChangedListener(new TextWatcherMediator(et_oldPass) {
                @Override
                public void afterTextChanged(Editable s) {
                    String result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        et_oldPass.setText(result);
                        et_oldPass.setSelection(result.length());
                    }
                }
            });
            et_newPass.addTextChangedListener(new TextWatcherMediator(et_newPass) {
                @Override
                public void afterTextChanged(Editable s) {
                    String result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        et_newPass.setText(result);
                        et_newPass.setSelection(result.length());
                    }
                }
            });
            et_confirmPass.addTextChangedListener(new TextWatcherMediator(et_confirmPass) {
                @Override
                public void afterTextChanged(Editable s) {
                    String result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        et_confirmPass.setText(result);
                        et_confirmPass.setSelection(result.length());
                    }
                }
            });
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
                                changePasswordApiCall(oldPass, newPass, dialog);
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

    @OnClick(R.id.iv_profileImg)
    void onProfileImgClick() {
        showTakeImagePopup();
    }

    @OnClick(R.id.btnUpdateAccount)
    void updateProfile() {
        String name = etName.getText().toString();
        String gender = etGender.getText().toString().trim().toUpperCase();
        String userName = etUserName.getText().toString().trim();
        String status = tv_status.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            textInputLayoutName.setError(getString(R.string.nick_name_can_not_be_left_blank));
        } else if (TextUtils.isEmpty(userName)) {
            textInputLayoutuserName.setError(getString(R.string.user_name_can_not_be_left_blank));
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                DataManager.getInstance().updateProfile(name, userName, "", gender, status, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        Logger.d(TAG, "Profile update onSuccess : " + response);
                        progressDialog.dismiss();
                        if (photoFile != null && photoFile.exists()) {
                            uploadImageApiCall(response.getUser());
                        } else {
                            SharedPreferenceManager.setUserObject(response.getUser());
                            Common.showAlertDialog(EditProfileActivity.this, response.getMessage(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPress();
                                }
                            }, false);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Logger.e(TAG, "Profile update error : " + error.getMessage());
                            Toast.makeText(EditProfileActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "Profile update error : " + baseResponse.getMessage());
                            Toast.makeText(EditProfileActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.btnDeleteAccount)
    void deleteAccount() {
        Common.showAlertDialog(this, getString(R.string.delet_account_confrim_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDeleteAccountDialog();
            }
        }, true);
    }

    private void showDeleteAccountDialog() {
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
                                deleteAccountApiCall(feedback, dialog);
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

    private void deleteAccountApiCall(String feedback, final Dialog parentDialog) {
        if (Common.isOnline(this)) {
            progressDialog.show();
            DataManager.getInstance().deleteAccountApiCall(feedback, new ResultListenerNG<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse response) {
                    Common.showAlertDialog(EditProfileActivity.this, response.getMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            parentDialog.dismiss();
                            LoginLogoutHandler.logoutUser(EditProfileActivity.this);
                        }
                    }, false);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Logger.e(TAG, "Delete Account error : " + error.getMessage());
                        Toast.makeText(EditProfileActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "Delete Account error : " + baseResponse.getMessage());
                        Toast.makeText(EditProfileActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void changePasswordApiCall(String oldPass, String newPass, final Dialog dialog) {
        if (Common.isOnline(this)) {
            progressDialog.show();
            DataManager.getInstance().changePassword(oldPass, newPass, new ResultListenerNG<UserResponse>() {
                @Override
                public void onSuccess(UserResponse response) {
                    progressDialog.dismiss();
                    Common.showAlertDialog(EditProfileActivity.this, response.getMessage(), null, false);
                    if (response.getUser() != null)
                        SharedPreferenceManager.setUserObject(response.getUser());
                    if (dialog != null)
                        dialog.dismiss();
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Logger.e(TAG, "change password error : " + error.getMessage());
                        Toast.makeText(EditProfileActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "change password error : " + baseResponse.getMessage());
                        Toast.makeText(EditProfileActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
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
                    Common.showAlertDialog(EditProfileActivity.this, getString(R.string.user_details_updated_successfully), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPress();
                        }
                    }, false);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Logger.e(TAG, "Image upload error : " + error.getMessage());
                        Toast.makeText(EditProfileActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "Image Upload error : " + baseResponse.getMessage());
                        Toast.makeText(EditProfileActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onBackPress() {
        finish();
        Common.hideKeyboard(this, etEmail);
        overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_from_right_animation);
    }
}
