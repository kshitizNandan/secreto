package com.secreto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.NetworkImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivityWithActionBar {
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_gender)
    TextView tv_gender;
    @BindView(R.id.tv_contact)
    TextView tv_contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_profile;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.my_profile);
    }

    private void init() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                iv_profileImg.setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
            } else {
                iv_profileImg.setDefaultImageResId(R.drawable.default_user);
            }
            tv_name.setText(user.getName());
            tv_status.setText(user.getCaption());
            tv_gender.setText(user.getGender());
            tv_contact.setText(user.getContact());
        }
    }

    @Override
    protected void onBackPress() {
        super.onBackPress();
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.out_from_right_animation);
    }
}
