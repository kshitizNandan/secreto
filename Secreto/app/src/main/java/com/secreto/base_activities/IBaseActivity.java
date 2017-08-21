package com.secreto.base_activities;

import android.support.v7.app.AppCompatActivity;

public abstract class IBaseActivity extends AppCompatActivity {
    /**
     * @return Resource id of view
     */
    public abstract int getLayoutResource();

    /**
     * Toolbar abstract method
     *
     * @return String of toolbar title
     */
    public abstract String getScreenTitle();

    /**
     * Toolbar abstract method
     *
     * @return boolean value true if show back button else false
     */
    public abstract boolean isShowHomeButton();

    protected abstract void onBackPress();
}
