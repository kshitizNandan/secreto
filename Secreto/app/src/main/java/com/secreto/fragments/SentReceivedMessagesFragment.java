package com.secreto.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.paginate.Paginate;
import com.secreto.R;
import com.secreto.activities.CreateMessageActivity;
import com.secreto.activities.FindUserActivity;
import com.secreto.adapters.SentOrReceivedMessagesRecyclerAdapter;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.Message;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.SendOrReceivedMessageResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.secreto.activities.HomeActivity.RC_SEND_MESSAGE;


public class SentReceivedMessagesFragment extends Fragment implements View.OnClickListener {
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
    @BindView(R.id.rl_progressBar)
    RelativeLayout rl_progressBar;
    private int offset;
    private boolean isLoading;
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
        getSendOrReceivedMsgApiCall();
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
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                getSendOrReceivedMsgApiCall();
            }
        });
    }

    private void setRecyclerAdapter() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        nAdapter = new SentOrReceivedMessagesRecyclerAdapter(objectArrayList, this);
        recyclerView.setAdapter(nAdapter);

        // Load More
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                getSendOrReceivedMsgApiCall();
                            }
                        }
                    }
                }
            }
        });

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
            if (isLoading) {
                rl_progressBar.setVisibility(View.VISIBLE);
            } else if (!swipeRefresh.isRefreshing()) {
                setLoadingLayout();
            }
            tvEmptyText.setVisibility(View.GONE);
            DataManager.getInstance().getSendOrReceivedMsgs(messageType, offset, new ResultListenerNG<SendOrReceivedMessageResponse>() {
                @Override
                public void onSuccess(SendOrReceivedMessageResponse response) {
                    if (offset == 0) {
                        objectArrayList.clear();
                    }
                    offset = response.getOffset();
                    if (response.getMessageArrayList() != null && !response.getMessageArrayList().isEmpty()) {
                        objectArrayList.addAll(response.getMessageArrayList());
                    } else {
                        tvEmptyText.setText(response.getMessage());
                        tvEmptyText.setVisibility(View.VISIBLE);
                    }
                    nAdapter.notifyDataSetChanged();
                    setMainLayout();
                    swipeRefresh.setRefreshing(false);
                    isLoading = false;
                    rl_progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(VolleyError error) {
                    isLoading = false;
                    swipeRefresh.setRefreshing(false);
                    rl_progressBar.setVisibility(View.GONE);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_reply:
                if (view.getTag() != null && view.getTag() instanceof User) {
                    User user = (User) view.getTag();
                    Intent intent = new Intent(getActivity(), CreateMessageActivity.class);
                    intent.putExtra(Constants.USER, user);
                    getActivity().startActivityForResult(intent, RC_SEND_MESSAGE);
                    getActivity().overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);

                }
                break;
        }
    }
}
