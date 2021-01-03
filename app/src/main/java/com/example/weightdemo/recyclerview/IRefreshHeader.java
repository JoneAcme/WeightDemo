package com.example.weightdemo.recyclerview;

import android.view.View;

import androidx.annotation.IntRange;

public interface IRefreshHeader {

    /**
     * 获取下拉刷新view
     */
    View getView();

    /**
     * 开始下拉刷新
     */
    void onRefreshStart();

    /**
     * 下拉刷新的进程 0--100
     * 这个过程伴随view的变化，请注意刷新的布局位置变化
     */
    void onPullProgress(int percent);

    /**
     * 进行下拉刷新等待的状态
     */
    void onRefreshing();

    /**
     * 刷新完成view的状态
     */
    void onRefreshDone();

    /**
     * 是否正在刷新中
     */
    boolean isRefreshing();

    /**
     * 刷新完成后的额外悬停时间
     */
    long getHoldTime();

    /**
     * 刷新控件高度
     */
    int getContentHeight();

    /**
     * 最大可下拉高度
     */
    int getMaxHeight();

    /**
     * 设置内容区域高度
     * @param value
     */
    void setContentHeight(int value);

    /**
     * 设置显示刷新 view 时的 percent 值
     * @param percent 0-100 的值
     */
    void setShowRefreshContentPercent(@IntRange(from = 0,to = 100) int percent);

    /**
     * 设置可下拉的最大高度
     * @param value
     */
    void setMaxHeight(int value);
}
