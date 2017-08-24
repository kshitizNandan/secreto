package com.secreto.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.secreto.R;
import com.secreto.common.MyApplication;

/**
 * Created by Aashish Tomar on 8/21/2017.
 */

public class CustomFontButton extends AppCompatButton {
    private TypedArray a = null;
    private int fontStyle;

    public CustomFontButton(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    private void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode()) {
            return;
        }
        try {
            this.a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFont, defStyle, 0);
            this.fontStyle = a.getInteger(R.styleable.CustomFont_fontStyle, 0);
        } finally {
            if (this.a != null)
                this.a.recycle();
        }

        switch (fontStyle) {
            case 1:
                setTypeface(MyApplication.fontHelveticaBold);
                break;
            case 2:
                setTypeface(MyApplication.fontHelveticaRegular);
                break;
            default:
                setTypeface(MyApplication.fontHelveticaRegular);
                break;
        }
    }
}
