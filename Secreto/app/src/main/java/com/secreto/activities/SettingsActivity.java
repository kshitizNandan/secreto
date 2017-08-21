package com.secreto.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivityWithActionBar {
    @BindView(R.id.navigation_view)
    NavigationView navigation_view;
    private SettingsActivity mActivity;

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

    private void loadNavHeader() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            View headerView = navigation_view.getHeaderView(0);
            ((TextView) headerView.findViewById(R.id.tv_name)).setText(user.getName());
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                ((NetworkImageView) headerView.findViewById(R.id.iv_profileImg)).setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
            } else {
                ((NetworkImageView) headerView.findViewById(R.id.iv_profileImg)).setDefaultImageResId(R.drawable.default_user);
            }
        }
    }

    private void handleNavigationItemClick() {
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        LoginLogoutHandler.logoutUser(mActivity);
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
        return getString(R.string.account);
    }

    @Override
    public boolean isShowHomeButton() {
        return true;
    }

}
