package com.example.weightdemo.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weightdemo.R;

public class RefreshHeaderView extends FrameLayout implements IRefreshHeader {

    private ImageView loadView;
    private TextView loadText;
    private Animation loadAnim;
    private int hasAnim = -1;
    private boolean isRefreshing = false;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.default_refresh_header_view, this);
        loadView = findViewById(R.id.refresh_loading_view);
        loadText = findViewById(R.id.refresh_title_view);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullProgress(int percent) {
        if(mPercent>0){
            if(percent>=mPercent){
                setVisibility(View.VISIBLE);
            }else{
                setVisibility(View.INVISIBLE);
            }
        }

        if (percent > 60) {
            loadText.setText("释放更新");
            if (hasAnim == -1) {
                hasAnim = 0;
                doAnim(R.anim.refresh_header_anim_change_up);
            }

        } else {
            loadText.setText("下拉刷新");
            if (hasAnim == 0) {
                hasAnim = -1;
                doAnim(R.anim.refresh_header_anim_change_down);
            }
        }

    }

    private void doAnim(int id) {
        loadAnim = AnimationUtils.loadAnimation(getContext(), id);
        LinearInterpolator lin = new LinearInterpolator();
        loadAnim.setInterpolator(lin);
        loadAnim.setFillAfter(true);
        loadView.startAnimation(loadAnim);
    }

    private void cancelAnim() {
        if (loadAnim != null) {
            loadAnim.cancel();
        }
    }

    @Override
    public void onRefreshing() {
        loadText.setText("加载中...");
        loadView.setImageResource(R.drawable.common_loading);
        doAnim(R.anim.story_progress_rotate);
        isRefreshing = true;
    }

    @Override
    public void onRefreshDone() {
        hasAnim = -1;
        cancelAnim();
        isRefreshing = false;
    }

    @Override
    public boolean isRefreshing() {
        return isRefreshing;
    }

    @Override
    public void onRefreshStart() {
        cancelAnim();
        hasAnim = -1;
        loadText.setText("下拉刷新");
        loadView.setImageResource(R.drawable.refresh_header_arrow_down);
    }
    private int contentHeight = dp2px(60);
    private int maxHeight = dp2px(100);
    private int mPercent = -1;
    @Override
    public int getContentHeight() {
        return contentHeight;
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public void setContentHeight(int value) {
        this.contentHeight = value;
    }

    @Override
    public void setShowRefreshContentPercent(int value) {
        this.mPercent = value;
    }

    @Override
    public void setMaxHeight(int value) {
        this.maxHeight = value;
    }

    @Override
    public long getHoldTime() {
        return 250;
    }

    public int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
