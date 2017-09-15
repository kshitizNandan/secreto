package com.secreto.base_activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.utils.CustomProgressDialog;

public abstract class BaseActivityWithActionBar extends IBaseActivity {

    private TextView toolbarTitle;
    protected CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        setToolbarWithBack();
        initProgressDialog();
    }

    private void initProgressDialog() {
        progressDialog = new CustomProgressDialog(this);
    }

    void setToolbarWithBack() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(isShowHomeButton());
                actionBar.setHomeAsUpIndicator(setHomeButtonDrawable());
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
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

    protected void setScreenTitle(String title) {
        if (toolbarTitle != null)
            toolbarTitle.setText(title);
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public boolean isShowHomeButton() {
        return true;
    }

    @Override
    protected void onBackPress() {
        finish();
    }

    @Override
    public void onBackPressed() {
        onBackPress();
    }

    @Override
    public int setHomeButtonDrawable() {
        return R.drawable.back_arrow_w;
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
