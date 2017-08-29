package com.secreto.activities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.secreto.R;
import com.secreto.common.SharedPreferenceManager;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 2000;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lLMain);
        linearLayout .clearAnimation();
        linearLayout .startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imgLogo);
        iv.clearAnimation();
        iv.startAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (SharedPreferenceManager.getUserObject() != null) {
                    intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}