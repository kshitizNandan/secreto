package com.secreto.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.secreto.R;
import com.secreto.common.MyApplication;

public class ExpandMessageDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ExpandMessageDialogFragment.class.getSimpleName();
    private ProgressDialog progressDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Point point = MyApplication.getScreenSize();
        final int width = point.x;
        dialog.setContentView(R.layout.fragment_sent_received_messages);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        init();
        initViews(dialog);
        setViews();
        return dialog;
    }

    private void init() {
    }

    private void initViews(Dialog dialog) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

    }

    private void setViews() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.tvSave:
//                updateUserName();
//                break;
//
//            case R.id.tvCancel:
//                dismiss();
//                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}
