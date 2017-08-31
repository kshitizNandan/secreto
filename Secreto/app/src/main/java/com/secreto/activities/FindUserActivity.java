package com.secreto.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
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
import com.secreto.responsemodel.AllUserResponse;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.CustomProgressDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindUserActivity extends BaseActivityWithActionBar implements View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final int RC_SEND_MESSAGE = 200;
    @BindView(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @BindView(R.id.input_layout_email_editText)
    TextInputLayout textInputLayoutEmail;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.recycler_view_search)
    RecyclerView recyclerViewSearch;

    private SearchUserAdapter searchAdapter;
    private ArrayList<Object> items = new ArrayList<>();
    private FindUserActivity mActivity;
    private ProgressDialog progressDialog;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_find_user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(this);
        searchView.setOnCloseListener(this);
        return true;
    }


    private void init() {
        progressDialog = new CustomProgressDialog(this);
        mActivity = this;
        recyclerViewSearch.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        searchAdapter = new SearchUserAdapter(items, this);
        recyclerViewSearch.setAdapter(searchAdapter);
    }

    @OnClick(R.id.btnSubmit)
    void findUserApiCall() {
        String userName = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            textInputLayoutEmail.setError(getString(R.string.user_name_can_not_be_left_blank));
        } else if (userName.equalsIgnoreCase(SharedPreferenceManager.getUserObject().getName()) || userName.equalsIgnoreCase(SharedPreferenceManager.getUserObject().getEmail())) {
            textInputLayoutEmail.setError(getString(R.string.you_can_not_send_message_to_yourself));
        } else {
            if (Common.isOnline(mActivity)) {
                progressDialog.show();
                DataManager.getInstance().findUser(userName, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        progressDialog.dismiss();
                        if (response.getUser() != null) {
                            Intent intent = new Intent(mActivity, CreateMessageActivity.class);
                            intent.putExtra(Constants.USER, response.getUser());
                            startActivityForResult(intent, RC_SEND_MESSAGE);
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

    private void getAllUsersApi(String name) {
        if (Common.isOnline(mActivity)) {
            DataManager.getInstance().getAllUsers(name, offset, new ResultListenerNG<AllUserResponse>() {
                @Override
                public void onSuccess(AllUserResponse response) {
                    if (response.getUsers() != null) {
                        if (items != null) {
                            items.clear();
                        }
                        items.addAll(response.getUsers());
                    } else {
                        textInputLayoutEmail.setError(response.getMessage());
                    }
                    searchAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_search:
                viewFlipper.setDisplayedChild(1);
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty())
            getAllUsersApi(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!query.isEmpty())
            getAllUsersApi(query);
        return true;
    }


    @Override
    public boolean onClose() {
        viewFlipper.setDisplayedChild(0);
        return false;
    }
}
