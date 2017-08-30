package com.secreto.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.secreto.R;
import com.secreto.model.User;
import com.secreto.viewHolders.BaseViewHolder;
import com.secreto.viewHolders.SearchUserViewHolder;

import java.util.List;

/**
 * Created by Kshitiz Nandan on 8/29/2017.
 */

public class SearchUserAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int SEARCH_USER = 1;
    private final List<Object> objectList;
    private View.OnClickListener onClickListener;

    public SearchUserAdapter(List<Object> objectList, View.OnClickListener onClickListener) {
        this.objectList = objectList;
        this.onClickListener=onClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case SEARCH_USER:
                return new SearchUserViewHolder(inflater.inflate(R.layout.search_user_item, parent, false),onClickListener);
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
        if (objectList.get(position) instanceof User) {
            return SEARCH_USER;
        }
        return -1;
    }
}
