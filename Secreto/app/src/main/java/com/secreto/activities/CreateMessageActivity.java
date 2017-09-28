package com.secreto.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.fragments.SentReceivedMessagesFragment;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateMessageActivity extends BaseActivityWithActionBar {
    public static final String TAG = CreateMessageActivity.class.getSimpleName();
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.allowCheckBox)
    CheckBox allowCheckBox;
    @BindView(R.id.etClue)
    EditText etClue;
    @BindView(R.id.tvAppName)
    TextView tvAppName;
    private String toUserId;
    private Activity mActivity;
    private String navFrom;
    private User user;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initView();
    }

    private void init() {
        mActivity = this;
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
        navFrom = getIntent().getStringExtra(Constants.NAVIGATION_FROM);
        user = (User) getIntent().getSerializableExtra(Constants.USER);
        if (user != null) {
            this.toUserId = user.getUserId();
            tvUserName.setText(!TextUtils.isEmpty(user.getName()) ? user.getName() : "");
            iv_profileImg.setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
        }
        allowCheckBox.setText(String.format(getString(R.string.allow_to_get_reply), user.getName()));
    }

    public static void startActivityForResult(Activity activity, User user, String navFrom, int REQUEST_CODE) {
        Intent intent = new Intent(activity, CreateMessageActivity.class);
        intent.putExtra(Constants.USER, user);
        intent.putExtra(Constants.NAVIGATION_FROM, navFrom);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void startActivity(Activity activity, User user, String navFrom) {
        Intent intent = new Intent(activity, CreateMessageActivity.class);
        intent.putExtra(Constants.USER, user);
        intent.putExtra(Constants.NAVIGATION_FROM, navFrom);
        activity.startActivity(intent);
    }


    private void initView() {
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
        if (navFrom.equalsIgnoreCase(SentReceivedMessagesFragment.TAG)) {
            iv_profileImg.setVisibility(View.GONE);
            etClue.setVisibility(View.GONE);
            tvUserName.setVisibility(View.GONE);
            tvAppName.setVisibility(View.VISIBLE);
            allowCheckBox.setText(String.format(getString(R.string.allow_to_get_reply), getString(R.string.sender)));
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_create_message;
    }

    @OnClick(R.id.iv_profileImg)
    void openUserProfileScreen() {
        if (user != null) {
            ProfileActivity.startActivity(mActivity, user);
            overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
        }
    }

    @OnClick(R.id.tvSend)
    void sendMessage() {
        String message = etMessage.getText().toString();
        String canReply = allowCheckBox.isChecked() ? "YES" : "NO";
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, R.string.message_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
        } else {
            if (Common.isOnline(this)) {
                progressDialog.show();
                String messageClue = etClue.getText().toString();
                String userId = SharedPreferenceManager.getUserObject().getUserId();
                DataManager.getInstance().sendMessage(userId,toUserId, message, messageClue, canReply, new ResultListenerNG<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        progressDialog.hide();
                        Common.showAlertDialog(mActivity, response.getMessage(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startHomeScreen(TAG);
                            }
                        }, false);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.hide();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Toast.makeText(CreateMessageActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateMessageActivity.this, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startHomeScreen(String tag) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.NAVIGATION_FROM, tag);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_from_right_animation);
        Common.hideKeyboard(mActivity, etMessage);
    }


    @Override
    protected void onBackPress() {
        finish();
        Common.hideKeyboard(mActivity, etMessage);
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_right_animation);
    }
}
