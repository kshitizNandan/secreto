package com.secreto.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.image.ImageCacheManager;
import com.secreto.model.User;
import com.secreto.utils.NetworkImageView;
import com.secreto.widgets.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends BaseActivityWithActionBar {
    @BindView(R.id.iv_profileImg)
    ImageView iv_profileImg;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_gender)
    TextView tv_gender;
    @BindView(R.id.tv_contact)
    TextView tv_contact;
    @BindView(R.id.tv_userName)
    TextView tv_userName;
    @BindView(R.id.tv_emailId)
    TextView tv_emailId;


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

    public static void startActivity(Activity activity, User user) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(Constants.USER, user);
        activity.startActivity(intent);
    }

    private void init() {
        User user = (User) getIntent().getSerializableExtra(Constants.USER);
        if (user != null) {
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(this, 80);
                Picasso.with(this).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_user));
            }
            tv_name.setText(user.getName());
            tv_status.setText(user.getCaption());
            tv_gender.setText(!TextUtils.isEmpty(user.getGender()) ? user.getGender().toLowerCase() : "-");
            tv_contact.setText(!TextUtils.isEmpty(user.getContact()) ? user.getContact() : "-");
            tv_userName.setText(user.getUserName());
            tv_emailId.setText(user.getEmail());
            if (!user.getUserId().equalsIgnoreCase(SharedPreferenceManager.getUserObject().getUserId())) {
                setScreenTitle(String.format(Locale.ENGLISH, getString(R.string.x_profile), user.getName()));
            }
        }
    }

    @Override
    protected void onBackPress() {
        super.onBackPress();
        finish();
        overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_from_right_animation);
    }
}
