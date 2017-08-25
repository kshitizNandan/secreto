package com.secreto.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.fragments.SentReceivedMessagesFragment;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class HomeActivity extends BaseActivityWithActionBar {

    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    private HomeActivity mActivity;
    @BindView(R.id.tabBar)
    TabLayout tabBar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private Fragment sentMessagesFragment, receivedMessagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setUserData();
        init();
        initViews();
    }

    private void setUserData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                iv_profileImg.setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
            } else {
                iv_profileImg.setDefaultImageResId(R.drawable.default_user);
            }
        }
    }

    private void init() {
        mActivity = this;
    }

    private void initViews() {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabBar.setupWithViewPager(viewPager);
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public boolean isShowHomeButton() {
        return false;
    }

    @OnClick(R.id.iv_profileImg)
    void onClickProfileImg() {
        Intent settingsActivityIntent = new Intent(mActivity, SettingsActivity.class);
        startActivity(settingsActivityIntent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.no_animation);
    }

    @OnLongClick(R.id.iv_profileImg)
    boolean showLogoutDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.custom_logout_dilaog, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);
        dialog.show();

        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            ((TextView) contentView.findViewById(R.id.tv_name)).setText(user.getName());
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                ((NetworkImageView) contentView.findViewById(R.id.iv_profileImg)).setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
            } else {
                ((NetworkImageView) contentView.findViewById(R.id.iv_profileImg)).setDefaultImageResId(R.drawable.default_user);
            }
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_cross:
                        dialog.dismiss();
                        break;
                    case R.id.tv_logout:
                        LoginLogoutHandler.logoutUser(mActivity);
                        break;
                }
            }
        };
        contentView.findViewById(R.id.iv_cross).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_logout).setOnClickListener(listener);
        return true;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    sentMessagesFragment = SentReceivedMessagesFragment.newInstance(Constants.SENT);
                    return sentMessagesFragment;
                case 1:
                    receivedMessagesFragment = SentReceivedMessagesFragment.newInstance(Constants.RECEIVED);
                    return receivedMessagesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = getString(R.string.sent);
                    break;
                case 1:
                    title = getString(R.string.received);
                    break;
            }
            return title;
        }
    }
}

