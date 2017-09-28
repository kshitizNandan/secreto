package com.secreto.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.VolleyError;
import com.secreto.R;
import com.secreto.activities.CreateMessageActivity;
import com.secreto.activities.ProfileActivity;
import com.secreto.adapters.SentOrReceivedMessagesRecyclerAdapter;
import com.secreto.common.Common;
import com.secreto.common.Constants;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.SendOrReceivedMessageResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SentReceivedMessagesFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    public static final String TAG = SentReceivedMessagesFragment.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int PERMISSION_ALL = 500;
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
    private LinearLayout layout;

    private int offset;
    private boolean isLoading;
    private String messageType;
    private SentOrReceivedMessagesRecyclerAdapter nAdapter;
    private MessageAndUserResponse response;
    private ArrayList<Object> objectArrayList = new ArrayList<>();
    private AlertDialog deleteDialog;
    private ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
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
        nAdapter = new SentOrReceivedMessagesRecyclerAdapter(objectArrayList, this, messageType);
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
            if (isLoading && !swipeRefresh.isRefreshing()) {
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
                    if (response.getMessageArrayList() != null) {
                        objectArrayList.addAll(response.getMessageArrayList());
                    }
                    if (!objectArrayList.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvEmptyText.setVisibility(View.GONE);
                    } else {
                        tvEmptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
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
            case R.id.ivMenu:
                if (view.getTag() != null && view.getTag() instanceof MessageAndUserResponse) {
                    layout = (LinearLayout) view.getParent();
                    response = (MessageAndUserResponse) view.getTag();
                    PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                    popupMenu.inflate(R.menu.message_menu);
                    try {
                        Field fieldPopup = popupMenu.getClass().getDeclaredField("mPopup");
                        fieldPopup.setAccessible(true);
                        MenuPopupHelper mPopup = (MenuPopupHelper) fieldPopup.get(popupMenu);
                        mPopup.setForceShowIcon(true);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception");
                    }
                    popupMenu.show();
                    if (messageType.equalsIgnoreCase(Constants.SENT) || response.getMessage().getCanReply().equalsIgnoreCase("NO")) {
                        popupMenu.getMenu().findItem(R.id.actionReply).setVisible(false);
                    } else {
                        popupMenu.getMenu().findItem(R.id.actionReply).setVisible(true);
                    }
                    popupMenu.setOnMenuItemClickListener(this);
                }
                break;
            case R.id.tvClue:
                if (view.getTag() != null && view.getTag() instanceof User) {
                    User user = (User) view.getTag();
                    ProfileActivity.startActivity(getActivity(), user);
                    getActivity().overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                }
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionReply:
                if (response != null && response.getMessage() != null) {
                    CreateMessageActivity.startActivity(getActivity(), new User().setUserId(response.getMessage().getUserId()), SentReceivedMessagesFragment.TAG);
                    getActivity().overridePendingTransition(R.anim.in_from_right_animation, R.anim.out_from_left_animation);
                }
                break;
            case R.id.actionDelete:
                if (response.getMessage() != null) {
                    if (deleteDialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.app_name))
                                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_message))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        deleteMessage();
                                    }

                                })
                                .setNegativeButton(getString(R.string.no), null);
                        deleteDialog = builder.create();
                    }
                    deleteDialog.show();
                }
                break;
            case R.id.actionShare:
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                break;
        }
        return false;
    }

    private void persistImage() {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bitmap = layout.getDrawingCache();

        String pathofBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, getString(R.string.app_name), null);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent intent1 = new Intent(android.content.Intent.ACTION_SEND);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent1.setType("image/*");
        startActivity(Intent.createChooser(intent1, getString(R.string.share_message_using)));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isPermissionGranted = true;
        switch (requestCode) {
            case PERMISSION_ALL:
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false;
                        break;
                    }
                }
                break;
        }
        if (isPermissionGranted) {
            persistImage();
        } else {
            Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMessage() {
        if (Common.isOnline(getActivity())) {
            progressDialog.show();
            DataManager.getInstance().deleteMessageApiCall(response.getMessage().getMessageId(), response.getMessageType(), new ResultListenerNG<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse response) {
                    progressDialog.dismiss();
                    if (!TextUtils.isEmpty(response.getMessage())) {
                        Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    refreshList();
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    BaseResponse statusMessage = Common.getStatusMessage(error);
                    if (statusMessage == null || TextUtils.isEmpty(statusMessage.getMessage())) {
                        Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), statusMessage.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}
