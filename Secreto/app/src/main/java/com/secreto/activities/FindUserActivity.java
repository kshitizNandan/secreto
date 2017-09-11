package com.secreto.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.adapters.SearchUserAdapter;
import com.secreto.base_activities.BaseActivityWithActionBar;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.mediatorClasses.TextWatcherMediator;
import com.secreto.model.User;
import com.secreto.responsemodel.AllUserResponse;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.CustomProgressDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindUserActivity extends BaseActivityWithActionBar implements View.OnClickListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    private static final int RC_SEND_MESSAGE = 200;
    private static final String TAG = FindUserActivity.class.getSimpleName();
    @BindView(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @BindView(R.id.input_layout_email_editText)
    TextInputLayout textInputLayoutEmail;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.recycler_view_search)
    RecyclerView recyclerViewSearch;
    @BindView(R.id.rl_progressBar)
    RelativeLayout rl_progressBar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvEmptyText)
    TextView tvEmptyText;

    private SearchUserAdapter searchAdapter;
    private ArrayList<Object> items = new ArrayList<>();
    private FindUserActivity mActivity;
    private ProgressDialog progressDialog;
    private MenuItem menuItem;
    private int offset;
    private boolean isLoading;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        setTextWatcher();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_find_user;
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, FindUserActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(this);
        menuItem = menu.findItem(R.id.menu_search);
        if (menuItem != null) {
            MenuItemCompat.setOnActionExpandListener(menuItem, this);
            MenuItemCompat.setActionView(menuItem, searchView);
        }
        return true;
    }

    private void init() {
        progressDialog = new CustomProgressDialog(this);
        mActivity = this;
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerViewSearch.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchUserAdapter(items, this);
        recyclerViewSearch.setAdapter(searchAdapter);

        // Load More
        recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount, totalItemCount, pastVisibleItems;
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isLoading = true;
                            if (offset != -1) {
                                getAllUsersApi();
                            }
                        }
                    }
                }
            }
        });
    }

    private void setTextWatcher() {
        etEmail.addTextChangedListener(new TextWatcherMediator(etEmail) {
            @Override
            public void onTextChanged(CharSequence s, View view) {
                if (!TextUtils.isEmpty(s.toString())) {
                    textInputLayoutEmail.setError("");
                }
            }
        });
    }

    @OnClick(R.id.btnSubmit)
    void findUserApiCall() {
        final String userName = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            textInputLayoutEmail.setError(getString(R.string.user_name_can_not_be_left_blank));
        } else if (userName.equals(SharedPreferenceManager.getUserObject().getUserName()) || userName.equals(SharedPreferenceManager.getUserObject().getEmail())) {
            textInputLayoutEmail.setError(getString(R.string.you_can_not_send_message_to_yourself));
        } else {
            if (Common.isOnline(mActivity)) {
                progressDialog.show();
                DataManager.getInstance().findUser(userName, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        progressDialog.dismiss();
                        if (response.getUser() != null) {
                            CreateMessageActivity.startActivityForResult(mActivity, response.getUser(), TAG, RC_SEND_MESSAGE);
                            overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                            finish();
                        } else {
                            textInputLayoutEmail.setError(response.getMessage());
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        progressDialog.dismiss();
                        BaseResponse baseResponse = Common.getStatusMessage(error);
                        if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                            Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAllUsersApi() {
        if (Common.isOnline(mActivity)) {
            if (isLoading) {
                rl_progressBar.setVisibility(View.VISIBLE);
            } else {
                offset = 0;
                progressBar.setVisibility(View.VISIBLE);
            }
            tvEmptyText.setVisibility(View.GONE);
            DataManager.getInstance().getAllUsers(keyword, offset, new ResultListenerNG<AllUserResponse>() {
                @Override
                public void onSuccess(AllUserResponse response) {
                    if (offset == 0) {
                        items.clear();
                    }
                    if (response.getUsers() != null && offset != -1)
                        items.addAll(response.getUsers());
                    if (!items.isEmpty()) {
                        recyclerViewSearch.setVisibility(View.VISIBLE);
                        tvEmptyText.setVisibility(View.GONE);
                    } else {
                        recyclerViewSearch.setVisibility(View.GONE);
                        tvEmptyText.setVisibility(View.VISIBLE);
                    }
                    offset = response.getOffset();
                    searchAdapter.notifyDataSetChanged();
                    isLoading = false;
                    rl_progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(VolleyError error) {
                    isLoading = false;
                    rl_progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        Toast.makeText(mActivity, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, baseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_search:
                viewFlipper.setDisplayedChild(1);
                break;
            case R.id.rLFindUser:
                if (view.getTag() != null && view.getTag() instanceof User) {
                    User user = (User) view.getTag();
                    if (!user.getUserId().equalsIgnoreCase(SharedPreferenceManager.getUserObject().getUserId())) {
                        CreateMessageActivity.startActivityForResult(mActivity, user, TAG, RC_SEND_MESSAGE);
                        overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                    } else {
                        textInputLayoutEmail.setError(getString(R.string.you_can_not_send_message_to_yourself));
                    }
                }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.keyword = query;
        if (!query.isEmpty()) {
            offset = 0;
            getAllUsersApi();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        this.keyword = query;
        if (!query.isEmpty()) {
            offset = 0;
            getAllUsersApi();
        }
        return true;
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if (items != null && items.isEmpty()) {
            tvEmptyText.setText(getString(R.string.search_user_message));
            tvEmptyText.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        viewFlipper.setDisplayedChild(0);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (menuItem != null)
            menuItem.collapseActionView();
    }

    @Override
    protected void onBackPress() {
        super.onBackPress();
        Common.hideKeyboard(mActivity, etEmail);
        overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_from_right_animation);
    }
}
