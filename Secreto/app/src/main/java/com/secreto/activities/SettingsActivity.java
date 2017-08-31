package com.secreto.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.base_activities.ImagePickerActivity;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.image.ImageCacheManager;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.model.User;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.utils.NetworkImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivityWithActionBar {
    @BindView(R.id.navigation_view)
    NavigationView navigation_view;
    private SettingsActivity mActivity;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        loadNavHeader();
        handleNavigationItemClick();
    }


    private void init() {
        mActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetUserData();
    }

    private void resetUserData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            ((TextView) headerView.findViewById(R.id.tv_name)).setText(user.getName());
            ((TextView) headerView.findViewById(R.id.tv_status)).setText(user.getCaption());
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                ((NetworkImageView) headerView.findViewById(R.id.iv_profileImg)).setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
            } else {
                ((NetworkImageView) headerView.findViewById(R.id.iv_profileImg)).setDefaultImageResId(R.drawable.default_user);
            }
        }
    }

    private void loadNavHeader() {
        headerView = navigation_view.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsActivityIntent = new Intent(mActivity, ProfileActivity.class);
                startActivity(settingsActivityIntent);
                overridePendingTransition(R.anim.in_from_right_animation, R.anim.no_animation);
            }
        });
    }


    private void handleNavigationItemClick() {
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        LoginLogoutHandler.logoutUser(mActivity);
                        break;
                    case R.id.nav_ediProfile:
                        Intent settingsActivityIntent = new Intent(mActivity, EditProfileActivity.class);
                        startActivity(settingsActivityIntent);
                        overridePendingTransition(R.anim.in_from_right_animation, R.anim.no_animation);
                        break;
                }
                return false;
            }
        });
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
