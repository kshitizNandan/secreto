package com.secreto.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.secreto.R;

import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
