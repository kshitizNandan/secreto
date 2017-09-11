package com.secreto.viewHolders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.common.Constants;
import com.secreto.model.Message;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.utils.DateFormatter;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class MessagesViewHolder extends BaseViewHolder {
    private final Context context;
    @BindView(R.id.tv_message)
    TextView tv_message;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tvClue)
    TextView tv_clue;
    @BindView(R.id.imgReply)
    ImageView img_reply;
    @BindView(R.id.llMessageItem)
    LinearLayout cardMessageItem;
    private String messageType;


    public MessagesViewHolder(View itemView, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, String messageType) {
        super(itemView);
        this.context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        img_reply.setOnClickListener(onClickListener);
        this.messageType = messageType;
        cardMessageItem.setOnLongClickListener(onLongClickListener);
        if (messageType.equalsIgnoreCase(Constants.SENT)) {
            tv_clue.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void onBindView(Object object, int position) {
        MessageAndUserResponse response = (MessageAndUserResponse) object;
        Message message = response.getMessage();
        tv_message.setText(message.getMessage());
        tv_time.setText(DateFormatter.getTimeString(message.getCreatedDate()));
        img_reply.setTag(response.getUser());
        cardMessageItem.setTag(response.getUser());
        tv_clue.setTag(response.getUser());
        if (messageType.equalsIgnoreCase(Constants.SENT)) {
            img_reply.setVisibility(View.GONE);
            if (response.getUser() != null && !TextUtils.isEmpty(response.getUser().getName()))
                tv_clue.setText(String.format(Locale.ENGLISH, context.getString(R.string.to_x), response.getUser().getName()));
        } else {
            if (!TextUtils.isEmpty(message.getMessageClue())) {
                tv_clue.setVisibility(View.VISIBLE);
                tv_clue.setText(message.getMessageClue());
            } else {
                tv_clue.setVisibility(View.GONE);
            }
            if (message.getCanReply().equalsIgnoreCase("YES")) {
                img_reply.setVisibility(View.VISIBLE);
            } else {
                img_reply.setVisibility(View.GONE);
            }
        }
    }
}
