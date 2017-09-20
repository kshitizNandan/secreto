package com.secreto.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.secreto.R;
import com.secreto.interfaces.ISetShareingMessageView;
import com.secreto.model.MessageAndUserResponse;
import com.secreto.viewHolders.BaseViewHolder;
import com.secreto.viewHolders.MessagesViewHolder;

import java.util.List;

/**
 * Created by Aashish Tomar on 8/25/2017.
 */

public class SentOrReceivedMessagesRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<Object> objectList;
    private static final int MESSAGE = 0;
    private View.OnClickListener onClickListener;
    private String messageType;

    public SentOrReceivedMessagesRecyclerAdapter(List<Object> objectList, View.OnClickListener onClickListener, String messageType) {
        this.objectList = objectList;
        this.onClickListener = onClickListener;
        this.messageType = messageType;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case MESSAGE:
                return new MessagesViewHolder(inflater.inflate(R.layout.message_item, parent, false), onClickListener, messageType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindView(objectList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (objectList.get(position) instanceof MessageAndUserResponse) {
            return MESSAGE;
        }
        return -1;
    }
}
