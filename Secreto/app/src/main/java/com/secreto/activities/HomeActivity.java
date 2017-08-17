package com.secreto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithTransparentActionBar;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.utils.LoginLogoutHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivityWithTransparentActionBar {
    @BindView(R.id.tv_name)
    TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tv_name.setText(SharedPreferenceManager.getUserObject().getFirstName());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        // To Hide Text showing on Long press
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.action_menu);
                if (v != null) {
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                openChatMorePopupWindow(findViewById(R.id.action_menu));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openChatMorePopupWindow(View id) {
        PopupMenu popup = new PopupMenu(this, id);
        popup.getMenuInflater().inflate(R.menu.home_menu_options, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_logout:
                        LoginLogoutHandler.logoutUser(HomeActivity.this);
                        break;
                }
                return false;
            }
        });
    }
}

