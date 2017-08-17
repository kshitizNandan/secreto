package com.secreto.base_activities;

import android.support.v7.app.AppCompatActivity;

public abstract class IBaseActivity extends AppCompatActivity {
    public abstract int getLayoutResource();

    public abstract String getScreenTitle();
}
