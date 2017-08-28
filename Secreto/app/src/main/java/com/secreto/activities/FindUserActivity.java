package com.secreto.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.CustomProgressDialog;
import com.secreto.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindUserActivity extends BaseActivityWithActionBar {

    private static final int RC_SEND_MESSAGE = 200;
    @BindView(R.id.input_layout_userName_editText)
    TextInputLayout input_layout_userName_editText;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.btnNext)
    Button btnNext;
    private CustomProgressDialog progressDialog;
    private FindUserActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTextWatcher();
        init();
    }

    private void setTextWatcher() {
        etUserName.addTextChangedListener(new TextWatcherMediator(etUserName) {
            @Override
            public void onTextChanged(CharSequence s, View view) {
                if (!TextUtils.isEmpty(s)) {
                    input_layout_userName_editText.setError("");
                }
            }
        });
    }

    private void init() {
        progressDialog = new CustomProgressDialog(this);
        mActivity = this;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_find_user;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.find_user);
    }

    @OnClick(R.id.btnNext)
    void findUserApiCall() {
        String userName = etUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            input_layout_userName_editText.setError(getString(R.string.user_name_can_not_be_left_blank));
        } else {
            if (Common.isOnline(mActivity)) {
                progressDialog.show();
                DataManager.getInstance().findUser(userName, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        progressDialog.dismiss();
                        if (response.getUser() != null) {
                            Intent intent = new Intent(mActivity, CreateMessageActivity.class);
                            intent.putExtra(Constants.USER, response.getUser());
                            startActivityForResult(intent, RC_SEND_MESSAGE);
                            overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                            finish();
                        } else {
                            input_layout_userName_editText.setError(response.getMessage());
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
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
    protected void onBackPress() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_right_animation);
    }

}
