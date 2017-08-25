package com.secreto.viewHolders;

import android.view.View;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.model.Message;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class MessagesViewHolder extends BaseViewHolder {
    @BindView(R.id.tv_message)
    TextView tv_message;

    public MessagesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBindView(Object object, int position) {
        Message message = (Message) object;
        tv_message.setText(message.getMessage());
    }
}
