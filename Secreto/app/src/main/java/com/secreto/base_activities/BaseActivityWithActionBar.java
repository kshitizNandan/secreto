package com.secreto.base_activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.secreto.R;

public class BaseActivityWithActionBar extends IBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        setToolbarWithBack();
    }

    void setToolbarWithBack() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(isShowHomeButton());
                actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
            if (toolbarTitle != null)
                toolbarTitle.setText(getScreenTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPress();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public int getLayoutResource() {
        return 0;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public boolean isShowHomeButton() {
        return false;
    }

    @Override
    protected void onBackPress() {
        finish();
    }
}
