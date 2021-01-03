package com.example.weightdemo.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weightdemo.R;

public class LoadMoreView extends FrameLayout implements ILoadMore {

    private View mLoading;
    private TextView mTextView;
    private Animation mLoadingAnimation;

    public LoadMoreView(@NonNull Context context) {
        this(context, null);
    }

    public LoadMoreView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.default_load_more_view, this);
        mLoading = findViewById(R.id.svs_load_more_loading);
        mTextView = findViewById(R.id.svs_load_more_text);
        mLoadingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.story_progress_rotate);
        mLoadingAnimation.setRepeatCount(-1);
        mLoadingAnimation.setInterpolator(new LinearInterpolator());
    }

    @Override
    public void changeToLoading() {
        mLoading.setVisibility(VISIBLE);
        mTextView.setText("");
    }

    @Override
    public void changeToLoadError() {
        mLoading.setVisibility(GONE);
        mTextView.setText(getContext().getString(R.string.story_cha_net_error));
    }

    @Override
    public void changeToDataEnd() {
        mLoading.setVisibility(GONE);
        mTextView.setText(getContext().getString(R.string.stream_aggregation_load_all));
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        if (isVisible && mLoading.getVisibility() == VISIBLE) {
            mLoadingAnimation.reset();
            mLoading.startAnimation(mLoadingAnimation);
        } else {
            mLoadingAnimation.cancel();
            mLoading.clearAnimation();
        }
        super.onVisibilityAggregated(isVisible);
    }

    @Override
    public View getView() {
        return this;
    }
}
