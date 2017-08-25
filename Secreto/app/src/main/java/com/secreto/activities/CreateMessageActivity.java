package com.secreto.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateMessageActivity extends BaseActivityWithActionBar {
    private static final String TAG = CreateMessageActivity.class.getSimpleName();
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.etMessage)
    EditText etMessage;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_create_message;
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, CreateMessageActivity.class);
        activity.startActivity(intent);
    }

    @OnClick(R.id.tvSend)
    void sendMessage() {

    }
}
