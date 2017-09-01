package com.secreto.paginate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.paginate.recycler.LoadingListItemCreator;
import com.secreto.R;

public class CustomLoadingListItemCreator implements LoadingListItemCreator {
    private View llForPaginate;
    private boolean hideIfFirstItem = false;

    public CustomLoadingListItemCreator() {
        this.hideIfFirstItem = false;
    }

    public CustomLoadingListItemCreator(boolean hideIfFirstItem) {
        this.hideIfFirstItem = hideIfFirstItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
        llForPaginate = view.findViewById(R.id.llForPaginate);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (hideIfFirstItem && position == 0) {
            llForPaginate.setVisibility(View.GONE);
        } else {
            llForPaginate.setVisibility(View.VISIBLE);
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }
}
