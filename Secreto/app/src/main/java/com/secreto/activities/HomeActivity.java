package com.secreto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.secreto.R;
import com.secreto.base_activities.BaseActivityWithTransparentActionBar;
import com.secreto.common.SharedPreferenceManager;

public class HomeActivity extends BaseActivityWithTransparentActionBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        SharedPreferenceManager.removeUserOnLogout();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }
}

