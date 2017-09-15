package com.secreto.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.model.User;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.widgets.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivityWithActionBar {
    @BindView(R.id.llHeaderView)
    LinearLayout llHeaderView;
    @BindView(R.id.toggleButton)
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        toggleButton.setChecked(SharedPreferenceManager.getNotificationService());
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Common.showAlertDialog(SettingsActivity.this, getString(R.string.off_notification_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferenceManager.setNotificationService(false);
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SharedPreferenceManager.setNotificationService(true);
                            toggleButton.setChecked(true);
                        }
                    });
                } else {
                    SharedPreferenceManager.setNotificationService(true);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        resetUserData();
    }

    private void resetUserData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            ((TextView) llHeaderView.findViewById(R.id.tv_name)).setText(user.getName());
            ((TextView) llHeaderView.findViewById(R.id.tv_status)).setText(user.getCaption());
            ImageView iv_profileImg = (ImageView) llHeaderView.findViewById(R.id.iv_profileImg);
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(this, 80);
                Picasso.with(this).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_user));
            }
        }
    }

    @OnClick(R.id.llHeaderView)
    void headerClick() {
        ProfileActivity.startActivity(SettingsActivity.this, SharedPreferenceManager.getUserObject());
        overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
    }

    @OnClick(R.id.tvEditProfile)
    void editProfile() {
        EditProfileActivity.startActivity(SettingsActivity.this);
        overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
    }

    @OnClick(R.id.tvShare)
    void shareProfile() {
        Common.shareProfile(SettingsActivity.this);
    }

    @OnClick(R.id.tvLogout)
    void logout() {
        LoginLogoutHandler.logoutUser(SettingsActivity.this);
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.settings);
    }

    @Override
    protected void onBackPress() {
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_bottom);
    }
}
