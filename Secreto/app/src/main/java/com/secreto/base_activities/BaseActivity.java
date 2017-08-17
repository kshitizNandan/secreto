package com.secreto.base_activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.utils.Logger;


public abstract class BaseActivity extends IBaseActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

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
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow_wh);

            TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
            if (toolbarTitle != null)
                toolbarTitle.setText(getScreenTitle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Logger.d(TAG, "home click");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
