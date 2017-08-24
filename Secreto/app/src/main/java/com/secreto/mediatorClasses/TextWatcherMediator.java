package com.secreto.mediatorClasses;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by Aashish Tomar on 6/13/2017.
 */

public abstract class TextWatcherMediator implements TextWatcher {
    private View view;

    public TextWatcherMediator(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged(s, view);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public abstract void onTextChanged(CharSequence s, View view);
}
