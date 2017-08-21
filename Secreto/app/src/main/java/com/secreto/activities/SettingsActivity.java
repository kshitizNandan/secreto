package com.secreto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;

public class SettingsActivity extends BaseActivityWithActionBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        return false;
    }

    @Override
    public boolean isShowToolbarTitle() {
        return false;
    }
}
