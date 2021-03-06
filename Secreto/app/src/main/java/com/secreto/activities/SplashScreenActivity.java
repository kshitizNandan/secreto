package com.secreto.activities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.mediatorClasses.AnimationMediator;

public class SplashScreenActivity extends AppCompatActivity {

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
        linearLayout.clearAnimation();
        linearLayout.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imgLogo);
        iv.clearAnimation();
        iv.startAnimation(anim);
        final TextView tvAppName = (TextView) findViewById(R.id.tvAppName);
        anim.setAnimationListener(new AnimationMediator() {
            @Override
            public void onAnimationEnd(Animation animation) {
                tvAppName.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.alpha);
                anim.reset();
                tvAppName.setAnimation(anim);
                tvAppName.startAnimation(anim);
            }
        });
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
                overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                finish();
            }
        }, 4000);
    }
}