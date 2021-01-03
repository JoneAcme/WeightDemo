package com.example.weightdemo.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WrapperAdapter extends RecyclerView.Adapter {

    private static final int REFRESH_TYPE = Integer.MIN_VALUE;
    private static final int HEADER_TYPE = Integer.MIN_VALUE + 1;
    private static final int FOOTER_TYPE = Integer.MAX_VALUE - 1;
    private static final int LOAD_MORE_TYPE = Integer.MAX_VALUE;

    private static final int HEAD_COUNT_SIZE = 2;
    private static final int FOOTER_COUNT_SIZE = 2;

    private final RecyclerView.Adapter mAdapter;
    private RefreshContentView refreshContentView;
    private LinearLayout headerContentLayout;
    private LinearLayout footerContentLayout;
    private FrameLayout loadMoreContentLayout;


    public WrapperAdapter(RecyclerView.Adapter mAdapter, RefreshContentView refreshView, LinearLayout headerLayout, LinearLayout footerLayout, FrameLayout loadMoreView) {
        this.mAdapter = mAdapter;
        this.refreshContentView = refreshView;
        this.headerContentLayout = headerLayout;
        this.footerContentLayout = footerLayout;
        this.loadMoreContentLayout = loadMoreView;

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                WrapperAdapter.this.notifyItemRangeChanged(positionStart + HEAD_COUNT_SIZE, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                WrapperAdapter.this.notifyItemRangeChanged(positionStart + HEAD_COUNT_SIZE, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                WrapperAdapter.this.notifyItemRangeInserted(positionStart + HEAD_COUNT_SIZE, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                WrapperAdapter.this.notifyItemRangeRemoved(positionStart + HEAD_COUNT_SIZE, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                WrapperAdapter.this.notifyItemMoved(fromPosition + HEAD_COUNT_SIZE, toPosition + HEAD_COUNT_SIZE);
            }

            @Override
            public void onChanged() {
                WrapperAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return REFRESH_TYPE;
        } else if (position == 1) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 2) {
            return FOOTER_TYPE;
        } else if (position == getItemCount() - 1) {
            return LOAD_MORE_TYPE;
        }
        return mAdapter.getItemViewType(getDataPosition(position));
//        return mAdapter.getItemViewType(position - 3);
    }

    public int getFooterCount() {
        return FOOTER_COUNT_SIZE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getDataFirstPosition() && position <= getDataLastPosition()) {
            mAdapter.onBindViewHolder(holder, getDataPosition(position));
        }
    }

    private int getDataPosition(int position) {
        return position - HEAD_COUNT_SIZE;
    }

    private int getDataFirstPosition() {
        return HEAD_COUNT_SIZE;
    }

    private int getDataLastPosition() {
        return getItemCount() - FOOTER_COUNT_SIZE - 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (position >= getDataFirstPosition() && position <= getDataLastPosition()) {
            mAdapter.onBindViewHolder(holder, position - HEAD_COUNT_SIZE, payloads);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == REFRESH_TYPE) {
            return new RefreshViewHolder(refreshContentView);
        } else if (viewType == HEADER_TYPE) {
            return new HeaderViewHolder(headerContentLayout);
        } else if (viewType == FOOTER_TYPE) {
            return new FooterViewHolder(footerContentLayout);
        } else if (viewType == LOAD_MORE_TYPE) {
            return new LoadMoreViewHolder(loadMoreContentLayout);
        }

        return mAdapter.createViewHolder(parent, viewType);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + HEAD_COUNT_SIZE + FOOTER_COUNT_SIZE;
    }

    private static class RefreshViewHolder extends RecyclerView.ViewHolder {
        public RefreshViewHolder(View view) {
            super(view);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        public LoadMoreViewHolder(View view) {
            super(view);
        }
    }
}
