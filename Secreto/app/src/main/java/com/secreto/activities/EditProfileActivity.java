package com.secreto.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.ImagePickerActivity;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.image.ImageCacheManager;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.MediaResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.CustomProgressDialog;
import com.secreto.utils.Logger;
import com.secreto.utils.NetworkImageView;
import com.secreto.widgets.CircleTransform;
import com.secreto.widgets.RoundedCornersTransform;
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
    @BindView(R.id.etMobile)
    EditText etMobile;
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
    @BindView(R.id.input_layout_mobile_editText)
    TextInputLayout textInputLayoutMobile;
    @BindView(R.id.etGender)
    EditText etGender;
    @BindView(R.id.tv_status)
    TextView tv_status;
    CustomProgressDialog progressDialog;
    EditProfileActivity mActivity;
    private File photoFile;
    private int genderSelection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        InitializeData();
    }

    private void init() {
        progressDialog = new CustomProgressDialog(this);
        mActivity = this;
    }

    private void InitializeData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            etName.setText(user.getName());
            etGender.setText(user.getGender());
            etMobile.setText(user.getContact());
            etUserName.setText(user.getUserName());
            etEmail.setText(user.getEmail());
            tv_status.setText(user.getCaption());
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(mActivity, 80);
                Picasso.with(mActivity).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.default_user));
            }
        }
    }


    @Override
    protected void onImageSet(File photoFile) {
        if (photoFile != null && photoFile.exists()) {
            this.photoFile = photoFile;
            int size = Common.dipToPixel(mActivity, 80);
            Picasso.with(mActivity).load(photoFile).transform(new CircleTransform()).resize(size, size).into(iv_profileImg);
        }
    }

    @Override
    protected CropAspectRatio getCropAspectRatio() {
        return null;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_edit_profile;
    }

    @OnClick(R.id.etGender)
    void genderSelctionDialog() {
        final CharSequence[] charSequences = getResources().getStringArray(R.array.gender_types);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity)
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
        et_status.setText(!TextUtils.isEmpty(tv_status.getText()) ? tv_status.getText().toString() : "");
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
        String mobile = etMobile.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String status = tv_status.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            textInputLayoutName.setError(getString(R.string.nick_name_can_not_be_left_blank));
        } else if (!TextUtils.isEmpty(mobile) && mobile.length() < 10) {
            textInputLayoutName.setError(getString(R.string.mobile_phone_number_should_be_of_10_digits));
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                DataManager.getInstance().updateProfile(name, mobile, gender, status, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        Logger.d(TAG, "Profile update onSuccess : " + response);
                        progressDialog.dismiss();
                        if (photoFile != null && photoFile.exists()) {
                            uploadImageApiCall(response.getUser());
                        } else {
                            SharedPreferenceManager.setUserObject(response.getUser());
                            Common.showAlertDialog(mActivity, response.getMessage(), new DialogInterface.OnClickListener() {
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
                            Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Logger.e(TAG, "Profile update error : " + baseResponse.getMessage());
                            Toast.makeText(mActivity, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePasswordApiCall(String oldPass, String newPass, final Dialog dialog) {
        if (Common.isOnline(this)) {
            progressDialog.show();
            DataManager.getInstance().changePassword(oldPass, newPass, new ResultListenerNG<UserResponse>() {
                @Override
                public void onSuccess(UserResponse response) {
                    progressDialog.dismiss();
                    Common.showAlertDialog(mActivity, response.getMessage(), null, false);
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
                        Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "change password error : " + baseResponse.getMessage());
                        Toast.makeText(mActivity, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Common.showAlertDialog(mActivity, getString(R.string.user_details_updated_successfully), new DialogInterface.OnClickListener() {
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
                        Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Logger.e(TAG, "Image Upload error : " + baseResponse.getMessage());
                        Toast.makeText(mActivity, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
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
        Common.hideKeyboard(mActivity, etEmail);
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_right_animation);
    }
}
