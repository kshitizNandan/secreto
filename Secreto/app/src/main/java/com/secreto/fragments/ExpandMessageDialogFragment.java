//package com.secreto.fragments;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.graphics.Point;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class ExpandMessageDialogFragment extends DialogFragment implements View.OnClickListener {
//    private static final String TAG = ExpandMessageDialogFragment.class.getSimpleName();
//    private EditText etFirstName, etLastName;
//    private TextView tvSave, tvCancel;
//    private ProgressDialog progressDialog;
//    private User user;
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        Point point = MyApplication.getScreenSize();
//        final int width = point.x;
//        dialog.setContentView(R.layout.fragment_update_user_name);
//        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
//        dialog.show();
//        init();
//        initViews(dialog);
//        setViews();
//        return dialog;
//    }
//
//    private void init() {
//        user = SharedPreferenceManager.getUserObject();
//    }
//
//    private void initViews(Dialog dialog) {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage(getString(R.string.please_wait));
//        progressDialog.setCancelable(false);
//        tvSave = (TextView) dialog.findViewById(R.id.tvSave);
//        tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
//        etFirstName = (EditText) dialog.findViewById(R.id.etFirstName);
//        etLastName = (EditText) dialog.findViewById(R.id.etLastName);
//    }
//
//    private void setViews() {
//        tvSave.setOnClickListener(this);
//        tvCancel.setOnClickListener(this);
//        if (!TextUtils.isEmpty(user.getFirstName())) {
//            etFirstName.setText(user.getFirstName());
//            etFirstName.setSelection(etFirstName.getText().length());
//        }
//        if (!TextUtils.isEmpty(user.getLastName())) {
//            etLastName.setText(user.getLastName());
//            etLastName.setSelection(etLastName.getText().length());
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.tvSave:
//                updateUserName();
//                break;
//
//            case R.id.tvCancel:
//                dismiss();
//                break;
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (progressDialog != null && progressDialog.isShowing())
//            progressDialog.dismiss();
//    }
//
//    private void updateUserName() {
//        final String firstName = etFirstName.getText().toString().trim();
//        final String lastName = etLastName.getText().toString().trim();
//        if (TextUtils.isEmpty(firstName)) {
//            Toast.makeText(getActivity(), R.string.first_name_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
//        } else if (!Common.isValidName(firstName)) {
//            Toast.makeText(getActivity(), R.string.entered_first_name_is_invalid, Toast.LENGTH_SHORT).show();
//        } else if (TextUtils.isEmpty(lastName)) {
//            Toast.makeText(getActivity(), R.string.last_name_can_not_be_left_blank, Toast.LENGTH_SHORT).show();
//        } else if (!Common.isValidName(lastName)) {
//            Toast.makeText(getActivity(), R.string.entered_last_name_is_invalid, Toast.LENGTH_SHORT).show();
//        } else {
//            if (Common.isOnline(getActivity())) {
//                progressDialog.show();
//                DataManager.getInstance().updateUserName(user.getUserId(), firstName, lastName, new ResultListenerNG<UserResponse>() {
//                    @Override
//                    public void onSuccess(UserResponse response) {
//                        Logger.d(TAG, "updateUserName onSuccess : " + response);
//                        progressDialog.dismiss();
//                        if (!TextUtils.isEmpty(response.getMessage())) {
//                            Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                        SharedPreferenceManager.setUserObject(response.getUser());
//                        if (getActivity() instanceof OnUserNameUpdated) {
//                            ((OnUserNameUpdated) getActivity()).onUserNameUpdated(firstName, lastName);
//                        }
//                        dismiss();
//                    }
//
//                    @Override
//                    public void onError(VolleyError error) {
//                        progressDialog.dismiss();
//                        StatusMessage statusMessage = Common.getStatusMessage(error);
//                        if (statusMessage == null || TextUtils.isEmpty(statusMessage.getMessage())) {
//                            Logger.e(TAG, "updateUserName error : " + error.getMessage());
//                            Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Logger.e(TAG, "updateUserName error : " + statusMessage.getMessage());
//                            Toast.makeText(getActivity(), statusMessage.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            } else {
//                Toast.makeText(getActivity(), R.string.check_your_internet_connection, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
