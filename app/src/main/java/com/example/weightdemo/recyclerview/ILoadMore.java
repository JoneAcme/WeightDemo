package com.example.weightdemo.recyclerview;

import android.view.View;

public interface ILoadMore {

    /**
     * 获取view
     */
    View getView();

    /**
     * 加载中
     */
    void changeToLoading();

    /**
     * 加载失败
     */
    void changeToLoadError();

    /**
     * 没有更多了
     */
    void changeToDataEnd();
}
