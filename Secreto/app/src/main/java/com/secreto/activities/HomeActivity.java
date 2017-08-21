package com.secreto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.utils.NetworkImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivityWithActionBar {
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.iv_profileImg)
    NetworkImageView iv_profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        iv_profileImg.setDefaultImageResId(R.drawable.default_user);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        User user = SharedPreferenceManager.getUserObject();
//        if (user != null) {
//            tv_name.setText(user.getName());
//            if (!TextUtils.isEmpty(user.getProfile_pic()))
//                iv_profileImg.setImageUrl(user.getProfile_pic(), ImageCacheManager.getInstance().getImageLoader());
//        }
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

    @Override
    public boolean isShowToolbarTitle() {
        return true;
    }

    @OnClick
    private void onClickProfileImg() {
        Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsActivityIntent);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.home_menu, menu);
//        // To Hide Text showing on Long press
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                final View v = findViewById(R.id.action_menu);
//                if (v != null) {
//                    v.setOnLongClickListener(new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View v) {
//                            return false;
//                        }
//                    });
//                }
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_menu:
//                openChatMorePopupWindow(findViewById(R.id.action_menu));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void openChatMorePopupWindow(View id) {
//        PopupMenu popup = new PopupMenu(this, id);
//        popup.getMenuInflater().inflate(R.menu.home_menu_options, popup.getMenu());
//        popup.show();
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.action_logout:
//                        LoginLogoutHandler.logoutUser(HomeActivity.this);
//                        break;
//                }
//                return false;
//            }
//        });
//    }
}

