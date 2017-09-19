package com.secreto.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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
import com.secreto.common.SharedPreferenceManager;
import com.secreto.data.DataManager;
import com.secreto.data.volley.ResultListenerNG;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.model.User;
import com.secreto.responsemodel.BaseResponse;
import com.secreto.responsemodel.SendOrReceivedMessageResponse;
import com.secreto.utils.LoginLogoutHandler;
import com.secreto.widgets.CircleTransform;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.SYSTEM_HEALTH_SERVICE;
import static com.secreto.activities.HomeActivity.RC_SEND_MESSAGE;


public class SentReceivedMessagesFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
    public static final String TAG = SentReceivedMessagesFragment.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
        nAdapter = new SentOrReceivedMessagesRecyclerAdapter(objectArrayList, this, this, messageType);
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
                if (messageType.equalsIgnoreCase(Constants.SENT)) {
                    popupMenu.getMenu().findItem(R.id.actionReply).setVisible(false);
                } else {
                    popupMenu.getMenu().findItem(R.id.actionReply).setVisible(true);
                }
                popupMenu.setOnMenuItemClickListener(this);
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

    boolean showOptionsDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                int size = Common.dipToPixel(getActivity(), 40);
                Picasso.with(getActivity()).load(user.getProfile_pic()).transform(new CircleTransform()).resize(size, size).placeholder(R.drawable.default_user).into(iv_profileImg);
            } else {
                iv_profileImg.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.default_user));
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
                        LoginLogoutHandler.logoutUserWithConfirm(getActivity());
                        break;
                }
            }
        };
        contentView.findViewById(R.id.iv_cross).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_logout).setOnClickListener(listener);
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionReply:
                break;
            case R.id.actionDelete:
                break;
            case R.id.actionShare:
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                break;
        }
        return false;
    }

    private void persistImage() {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.message_item, null);
//        RelativeLayout rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

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

}
