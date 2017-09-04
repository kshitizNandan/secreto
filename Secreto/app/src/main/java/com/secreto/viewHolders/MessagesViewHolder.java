package com.secreto.viewHolders;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.secreto.R;
import com.secreto.common.SharedPreferenceManager;
import com.secreto.model.Message;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.model.User;
import com.secreto.utils.DateFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class MessagesViewHolder extends BaseViewHolder {
    @BindView(R.id.tv_message)
    TextView tv_message;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_clue)
    TextView tv_clue;
    @BindView(R.id.img_reply)
    ImageView img_reply;


    public MessagesViewHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        img_reply.setOnClickListener(onClickListener);
    }

    @Override
    public void onBindView(Object object, int position) {
        MessageAndUserResponse response = (MessageAndUserResponse) object;
        if (response.getMessage() != null) {
            Message message = response.getMessage();
            if (message.getCanReply().equalsIgnoreCase("YES")) {
                img_reply.setVisibility(View.VISIBLE);
            } else {
                img_reply.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(message.getMessageClue())) {
                tv_clue.setVisibility(View.VISIBLE);
                tv_clue.setText(message.getMessageClue());
            } else {
                tv_clue.setVisibility(View.GONE);
            }
            tv_message.setText(message.getMessage());
            tv_time.setText(DateFormatter.getTimeString(message.getCreatedDate()));
        }
        if (response.getUser() != null) {
            User user = response.getUser();
            img_reply.setTag(user);
            if (!TextUtils.equals(SharedPreferenceManager.getUserObject().getUserId(), user.getUserId())) {
                img_reply.setVisibility(View.GONE);
                tv_clue.setVisibility(View.VISIBLE);
                tv_clue.setText(user.getName());
            }
        }
    }
}
