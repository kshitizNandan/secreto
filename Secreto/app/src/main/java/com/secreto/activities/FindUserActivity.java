package com.secreto.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.UserResponse;
import com.secreto.utils.CustomProgressDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindUserActivity extends BaseActivityWithActionBar implements View.OnClickListener, SearchView.OnQueryTextListener, SearchManager.OnCancelListener {

    private static final int RC_SEND_MESSAGE = 200;
    @BindView(R.id.etSearch)
    EditText edtSearch;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgSearchCross)
    ImageView imgCross;
    @BindView(R.id.recycler_view_search)
    RecyclerView recyclerViewSearch;

    private SearchUserAdapter searchAdapter;
    private ArrayList<Object> items = new ArrayList<>();
    private CustomProgressDialog progressDialog;
    private FindUserActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        findUserApiCall();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_find_user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchManager.setOnCancelListener(this);
        return true;
    }


    private void init() {
        mActivity = this;
        recyclerViewSearch.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        searchAdapter = new SearchUserAdapter(items, this);
        recyclerViewSearch.setAdapter(searchAdapter);
    }


    //    @OnClick(R.id.btnNext)
    void findUserApiCall() {
        String userName = edtSearch.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            //   input_layout_userName_editText.setError(getString(R.string.user_name_can_not_be_left_blank));
        } else if (userName.equalsIgnoreCase(SharedPreferenceManager.getUserObject().getName()) || userName.equalsIgnoreCase(SharedPreferenceManager.getUserObject().getEmail())) {
            // input_layout_userName_editText.setError(getString(R.string.you_can_not_send_message_to_yourself));
        } else {
            if (Common.isOnline(mActivity)) {
                progressDialog.show();
                DataManager.getInstance().findUser(userName, new ResultListenerNG<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        progressDialog.dismiss();
                        if (response.getUser() != null) {
                            if (items != null) {
                                items.clear();
                            }
                            items.add(response.getUser());
                            searchAdapter.notifyDataSetChanged();
                            Intent intent = new Intent(mActivity, CreateMessageActivity.class);
                            intent.putExtra(Constants.USER, response.getUser());
                            startActivityForResult(intent, RC_SEND_MESSAGE);
                            overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                            finish();
                        } else {

//                            input_layout_userName_editText.setError(response.getMessage());
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


    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onCancel() {

    }
}
