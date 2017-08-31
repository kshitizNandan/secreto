package com.secreto.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.secreto.R;
import com.secreto.adapters.MyFragmentPagerAdapter;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.fragments.SentReceivedMessagesFragment;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.utils.NetworkImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class HomeActivity extends BaseActivityWithActionBar {

    private static final int RC_SEND_MESSAGE = 200;
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    private HomeActivity mActivity;
    @BindView(R.id.tabBar)
    TabLayout tabBar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fabComposeMessage)
    FloatingActionButton fabComposeMessage;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private boolean exitFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetUserData();
    }

    private void resetUserData() {
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
        String[] titles = getResources().getStringArray(R.array.message_types);
        fragmentArrayList.add(SentReceivedMessagesFragment.newInstance(Constants.RECEIVED));
        fragmentArrayList.add(SentReceivedMessagesFragment.newInstance(Constants.SENT));
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList, titles));
        tabBar.setupWithViewPager(viewPager);
        // fab compose Click
        fabComposeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, FindUserActivity.class);
                startActivityForResult(intent, RC_SEND_MESSAGE);
                overridePendingTransition(R.anim.in_from_right_animation, R.anim.no_animation);
            }
        });
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


    @Override
    public void onBackPressed() {
        if (exitFlag) {
            Toast.makeText(mActivity, getString(R.string.back_pressagain_to_exit), Toast.LENGTH_SHORT).show();
            exitFlag = false;
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!fragmentArrayList.isEmpty()) {
//            for (Fragment fragment : fragmentArrayList) {
//                if (fragment instanceof SentReceivedMessagesFragment) {
//                    SentReceivedMessagesFragment fragment1 = (SentReceivedMessagesFragment) fragment;
//                    fragment1.getSendOrReceivedMsgApiCall();
//                }
//            }
//        }
//    }
}

