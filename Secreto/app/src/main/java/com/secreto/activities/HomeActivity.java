package com.secreto.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.secreto.R;
import com.secreto.SocialSharing.SocialSharingClass;
import com.secreto.adapters.MyFragmentPagerAdapter;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.fragments.SentReceivedMessagesFragment;
import com.secreto.model.User;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.widgets.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.github.yavski.fabspeeddial.FabSpeedDial;

public class HomeActivity extends BaseActivityWithActionBar {
    public static final int RC_SEND_MESSAGE = 200;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    @BindView(R.id.iv_profileImg)
    ImageView iv_profileImg;
    @BindView(R.id.tabBar)
    TabLayout tabBar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.floatButton)
    FabSpeedDial composeMessageButton;
    @BindView(R.id.overlayView)
    View overlayView;
    @BindView(R.id.test)
    ImageView imageView;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private boolean exitFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.REFRESH_LIST_BROADCAST));
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetUserData();
    }

    private void resetUserData() {
        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(this, 80);
                Picasso.with(this).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_user));
            }
        }
    }


    private void initViews() {
        String[] titles = getResources().getStringArray(R.array.message_types);
        fragmentArrayList.add(SentReceivedMessagesFragment.newInstance(Constants.RECEIVED));
        fragmentArrayList.add(SentReceivedMessagesFragment.newInstance(Constants.SENT));
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentArrayList, titles));
        tabBar.setupWithViewPager(viewPager);
        // Float button Listener
        composeMessageButton.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                overlayView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_share:
                        imageView.setImageBitmap(drawTextToBitmap(HomeActivity.this, R.drawable.logo, "this is test on image"));

//                        Common.shareImage(HomeActivity.this);
//                        final String sharingMessage = String.format(Locale.ENGLISH, getString(R.string.hey_guys_please_share_your_views_about_me), SharedPreferenceManager.getUserObject().getUserName());
//                        new SocialSharingClass(HomeActivity.this, new SocialSharingClass.OnItemSelectedItemListener() {
//                            @Override
//                            public void selectedItem(String item) {
//                                if (item.equalsIgnoreCase(SocialSharingClass.FACEBOOK)) {
//                                    SocialSharingClass.ShareOnFacebook(HomeActivity.this, sharingMessage, callbackManager, new FacebookCallback<Sharer.Result>() {
//                                        @Override
//                                        public void onSuccess(Sharer.Result result) {
//                                            Toast.makeText(HomeActivity.this, "success", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onCancel() {
//                                        }
//
//                                        @Override
//                                        public void onError(FacebookException error) {
//                                            Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                } else if (item.equalsIgnoreCase(SocialSharingClass.TWITTER)) {
//                                    SocialSharingClass.ShareOnTwitter(HomeActivity.this, sharingMessage);
//                                } else if (item.equalsIgnoreCase(SocialSharingClass.LINKEDIN)) {
//                                }
//                            }
//                        });
                        break;
                    case R.id.action_sendMessage:
                        FindUserActivity.startActivity(HomeActivity.this);
                        overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                        break;
                }
                overlayView.setVisibility(View.GONE);
                return true;
            }

            @Override
            public void onMenuClosed() {
                overlayView.setVisibility(View.GONE);
            }
        });
    }

    public Bitmap drawTextToBitmap(Context mContext, int resourceId, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110, 110, 110));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 6;
            int y = (bitmap.getHeight() + bounds.height()) / 5;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception


            return null;
        }

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public String getScreenTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public boolean isShowHomeButton() {
        return false;
    }

    @OnClick(R.id.iv_profileImg)
    void onClickProfileImg() {
        Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsActivityIntent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.no_animation);
    }


    @OnLongClick(R.id.iv_profileImg)
    boolean showLogoutDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.custom_logout_dilaog, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);
        dialog.show();

        User user = SharedPreferenceManager.getUserObject();
        if (user != null) {
            ((TextView) contentView.findViewById(R.id.tv_name)).setText(user.getName());
            ((TextView) contentView.findViewById(R.id.tv_status)).setText(user.getCaption());
            ImageView iv_profileImg = (ImageView) contentView.findViewById(R.id.iv_profileImg);
            if (!TextUtils.isEmpty(user.getProfile_pic())) {
                int size = Common.dipToPixel(this, 40);
                Picasso.with(this).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_user));
            }

        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_cross:
                        dialog.dismiss();
                        break;
                    case R.id.tv_logout:
                        LoginLogoutHandler.logoutUser(HomeActivity.this);
                        break;
                }
            }
        };
        contentView.findViewById(R.id.iv_cross).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_logout).setOnClickListener(listener);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (exitFlag) {
            Toast.makeText(this, getString(R.string.back_pressagain_to_exit), Toast.LENGTH_SHORT).show();
            exitFlag = false;
        } else {
            super.onBackPressed();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSentOrReceivedMessageList();
        }
    };

    private void refreshSentOrReceivedMessageList() {
        if (!fragmentArrayList.isEmpty()) {
            for (Fragment fragment : fragmentArrayList) {
                if (fragment instanceof SentReceivedMessagesFragment) {
                    SentReceivedMessagesFragment fragment1 = (SentReceivedMessagesFragment) fragment;
                    fragment1.refreshList();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook Callback
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

}

