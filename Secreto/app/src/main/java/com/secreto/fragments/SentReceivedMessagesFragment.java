package com.secreto.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.paginate.Paginate;
import com.secreto.R;
import com.secreto.adapters.SentOrReceivedMessagesRecyclerAdapter;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.paginate.CustomLoadingListItemCreator;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.SendOrReceivedMessageResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SentReceivedMessagesFragment extends Fragment implements Paginate.Callbacks {
    @BindView(R.id.llForOfflineScreen)
    LinearLayout llForOfflineScreen;
    @BindView(R.id.rlForLoadingScreen)
    RelativeLayout rlForLoadingScreen;
    @BindView(R.id.svMain)
    RelativeLayout svMain;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvEmptyText)
    TextView tvEmptyText;
    @BindView(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private int offset = 0;
    private boolean loading = false;
    private String messageType;
    private SentOrReceivedMessagesRecyclerAdapter nAdapter;
    private ArrayList<Object> objectArrayList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            messageType = getArguments().getString(Constants.MESSAGE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sent_received_messages, container, false);
        ButterKnife.bind(this, rootView);
        setRecyclerAdapter();
        init();
        return rootView;
    }

    public static SentReceivedMessagesFragment newInstance(String messageType) {
        SentReceivedMessagesFragment fragment = new SentReceivedMessagesFragment();
        Bundle args = new Bundle();
        args.putString(Constants.MESSAGE_TYPE, messageType);
        fragment.setArguments(args);
        return fragment;
    }

    private void init() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                getSendOrReceivedMsgApiCall();
            }
        });
    }

    private void setRecyclerAdapter() {
        nAdapter = new SentOrReceivedMessagesRecyclerAdapter(objectArrayList);
        recyclerView.setAdapter(nAdapter);
        Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator(true)).build();
    }

    @OnClick(R.id.tvRetry)
    void onClickRetry() {
        refreshList();
    }

    public void refreshList() {
        offset = 0;
        getSendOrReceivedMsgApiCall();
    }

    public void getSendOrReceivedMsgApiCall() {
        if (Common.isOnline(getActivity())) {
            if (offset == -1) {
                return;
            }
            loading = true;
            if (offset == 0) {
                setLoadingLayout();
            }
            if (!swipeRefresh.isRefreshing())
                setLoadingLayout();
            tvEmptyText.setVisibility(View.GONE);
            DataManager.getInstance().getSendOrReceivedMsgs(messageType, offset, new ResultListenerNG<SendOrReceivedMessageResponse>() {
                @Override
                public void onSuccess(SendOrReceivedMessageResponse response) {
                    if (offset == 0) {
                        objectArrayList.clear();
                    }
                    offset = response.getOffset();
                    loading = false;
                    if (response.getMessageArrayList() != null && !response.getMessageArrayList().isEmpty()) {
                        objectArrayList.addAll(response.getMessageArrayList());
                        nAdapter.notifyDataSetChanged();
                    } else {
                        tvEmptyText.setText(response.getMessage());
                        tvEmptyText.setVisibility(View.VISIBLE);
                    }
                    setMainLayout();
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onError(VolleyError error) {
                    loading = false;
                    swipeRefresh.setRefreshing(false);
                    BaseResponse baseResponse = Common.getStatusMessage(error);
                    if (baseResponse == null || TextUtils.isEmpty(baseResponse.getMessage())) {
                        setOfflineLayout(getString(R.string.something_went_wrong));
                    } else {
                        setOfflineLayout(baseResponse.getMessage());
                    }
                }
            });
        } else {
            setOfflineLayout(getString(R.string.check_your_internet_connection));
        }
    }


    private void setOfflineLayout(String message) {
        tvEmptyText.setText(message);
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(llForOfflineScreen));
    }

    private void setLoadingLayout() {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(rlForLoadingScreen));
    }

    private void setMainLayout() {
        viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(svMain));
    }

    @Override
    public void onLoadMore() {
        if (!loading) {
            getSendOrReceivedMsgApiCall();
        }
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return offset == -1;
    }

}
