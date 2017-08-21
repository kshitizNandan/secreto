package com.secreto.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.secreto.R;

/**
 * Created by Aashish Tomar on 8/21/2017.
 */

public class CustomProgressDialog extends ProgressDialog {
    public CustomProgressDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setMessage(context.getString(R.string.please_wait));
        this.setCancelable(false);
    }
}
