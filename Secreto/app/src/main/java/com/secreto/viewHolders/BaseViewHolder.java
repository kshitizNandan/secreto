package com.secreto.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(Object object, int position);
}
