package com.secreto.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;

public class CreateMessageActivity extends BaseActivityWithActionBar{

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_create_message;
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, CreateMessageActivity.class);
        activity.startActivity(intent);
    }
}
