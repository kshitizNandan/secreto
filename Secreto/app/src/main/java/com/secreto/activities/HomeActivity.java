package com.secreto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithTransparentActionBar;

public class HomeActivity extends BaseActivityWithTransparentActionBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public String getScreenTitle() {
        return null;
    }
}

