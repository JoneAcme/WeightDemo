package com.example.weightdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weightdemo.recyclerview.ExRecyclerView;
import com.example.weightdemo.recyclerview.WBLiveRecyclerViewListener;
import com.example.weightdemo.recyclerview.WrapperAdapter;

import java.util.ArrayList;
import java.util.List;

public class WBRefreshActivity extends AppCompatActivity {

    private ExRecyclerView mExRecyclerView;
    private List<String> mStringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_b_refresh);
        mExRecyclerView = findViewById(R.id.mExRecyclerView);
        initData();
        MyAdapter myAdapter = new MyAdapter(this, mStringList);
        mExRecyclerView.setAdapter(myAdapter);
        mExRecyclerView.setPullToRefreshListener(() -> mExRecyclerView.postDelayed(() -> mExRecyclerView.endRefresh(), 3000));
        myAdapter.setRecyclerListener(new WBLiveRecyclerViewListener() {
            @Override
            public void onScrollBottom() {
                Log.e("tagggg", "onScrollBottom");
                initData();
            }

            @Override
            public void onItemClick(int position) {

            }
        });
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<String> date;
        private Context mContext;
        protected WBLiveRecyclerViewListener recyclerListener;


        public MyAdapter(Context mContext, List<String> date) {
            this.mContext = mContext;
            this.date = date;
        }

        public void setRecyclerListener(WBLiveRecyclerViewListener recyclerListener) {
            this.recyclerListener = recyclerListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.normal_item, null,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            onScrollBottom(position);
        }
        /**
         * 上拉加载更多
         *
         * @param position
         */
        private final void onScrollBottom(int position) {
            if (recyclerListener == null) {
                return;
            }
            if (position == getItemCount() - 1) {
                recyclerListener.onScrollBottom();
            }
        }
        @Override
        public int getItemCount() {
            return date == null ? 0 : date.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }


    private void initData() {
        for (int i = 0; i < 15; i++) {
            mStringList.add("");
        }
    }
}