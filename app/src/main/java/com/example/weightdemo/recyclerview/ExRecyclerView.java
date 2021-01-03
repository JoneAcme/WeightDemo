package com.example.weightdemo.recyclerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;

public class ExRecyclerView extends RecyclerView {

    //刷新，header，footer，more 的容器
    private RefreshContentView refreshContentView;
    private LinearLayout headerContentLayout;
    private LinearLayout footerContentLayout;
    private FrameLayout loadMoreContentLayout;
    //刷新控件，loadmore控件
    private IRefreshHeader refreshView;
    private ILoadMore loadMore;
    //flag
    private boolean refreshEnable = true;
    private boolean loadMoreEnable = true;
    //状态
    private RefreshState refreshState = RefreshState.NORMAL;

    private int touchDownPointY = -1;
    private PullToRefreshListener refreshListener;

    private int mScrollPointerId;
    private int mInitialTouchX, mInitialTouchY;
    private int mTouchSlop;
    private int mPreLoadNumber = 1;
    private boolean downYCanDoRefresh;



    public ExRecyclerView(Context context) {
        this(context, null);
    }

    public ExRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setLayoutManager(new LinearLayoutManager(context));
        initView();
    }

    private void initView() {
        initRefreshContent();
        initHeaderContent();
        initFooterContent();
        initLoadMoreContent();

        initDefaultRefreshView();
        initDefaultLoadMoreView();
    }

    private void initRefreshContent() {
        if (refreshContentView == null) {
            refreshContentView = new RefreshContentView(getContext());
            refreshContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initHeaderContent() {
        if (headerContentLayout == null) {
            headerContentLayout = new LinearLayout(getContext());
            headerContentLayout.setOrientation(LinearLayout.VERTICAL);
            headerContentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initFooterContent() {
        if (footerContentLayout == null) {
            footerContentLayout = new LinearLayout(getContext());
            footerContentLayout.setOrientation(LinearLayout.VERTICAL);
            footerContentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initLoadMoreContent() {
        if (loadMoreContentLayout == null) {
            loadMoreContentLayout = new FrameLayout(getContext());
            loadMoreContentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        }
    }

    private void initDefaultRefreshView() {
        IRefreshHeader refreshHeaderView = new RefreshHeaderView(getContext());
        setRefreshView(refreshHeaderView);
    }

    private void initDefaultLoadMoreView() {
        LoadMoreView loadMoreView = new LoadMoreView(getContext());
        setLoadMoreView(loadMoreView);
    }

    public void setRefreshView(IRefreshHeader iRefreshHeader) {
        if (iRefreshHeader == null || iRefreshHeader.getView() == null) {
            throw new RuntimeException("refresh header can not be null");
        }
        refreshContentView.removeAllViews();
        refreshView = iRefreshHeader;
        refreshContentView.addView(iRefreshHeader.getView());
    }

    public void setLoadMoreView(ILoadMore iLoadMore) {
        if (iLoadMore == null || iLoadMore.getView() == null) {
            throw new RuntimeException("load more view can not be null");
        }
        loadMoreContentLayout.removeAllViews();
        loadMore = iLoadMore;
        loadMoreContentLayout.addView(iLoadMore.getView());
    }

    public void addHeadView(View view) {
        if (view == null) {
            return;
        }
        if (view.getParent() != null) {
            throw new RuntimeException("header already has a parent");
        }
        headerContentLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    public void removeHeadView(View view) {
        if (view == null || view.getParent() == null) {
            return;
        }
        if (view.getParent() == headerContentLayout) {
            headerContentLayout.removeView(view);
        }
    }

    public void addFooterView(View view) {
        if (view == null) {
            return;
        }
        if (view.getParent() != null) {
            throw new RuntimeException("footer already has a parent");
        }
        footerContentLayout.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    public void removeFooterView(View view) {
        if (view == null || view.getParent() == null) {
            return;
        }
        if (view.getParent() == footerContentLayout) {
            footerContentLayout.removeView(view);
        }
    }

    private WrapperAdapter mAdapter;

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = new WrapperAdapter(adapter, refreshContentView, headerContentLayout, footerContentLayout, loadMoreContentLayout);
        super.setAdapter(mAdapter);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int touchY = (int) e.getY();
        touchDownPointY = touchY;
        boolean canScrollHorizontally = getLayoutManager().canScrollHorizontally();
        boolean canScrollVertically = getLayoutManager().canScrollVertically();
        int action = e.getActionMasked();
        int actionIndex = e.getActionIndex();
        switch (action) {
            case ACTION_DOWN:
                mScrollPointerId = e.getPointerId(0);
                this.mInitialTouchX = (int) (e.getX() + 0.5F);
                this.mInitialTouchY = (int) (e.getY() + 0.5F);
                return super.onInterceptTouchEvent(e);

            case ACTION_MOVE:
                int index = e.findPointerIndex(this.mScrollPointerId);
                if (index < 0) {
                    return false;
                }

                int x = (int) (e.getX(index) + 0.5F);
                int y = (int) (e.getY(index) + 0.5F);
                if (getScrollState() != SCROLL_STATE_DRAGGING) {
                    int dx = x - this.mInitialTouchX;
                    int dy = y - this.mInitialTouchY;
                    boolean startScroll = false;
                    if (canScrollHorizontally && Math.abs(dx) > this.mTouchSlop && Math.abs(dx) > Math.abs(dy)) {
                        startScroll = true;
                    }

                    if (canScrollVertically && Math.abs(dy) > this.mTouchSlop && Math.abs(dy) > Math.abs(dx)) {
                        startScroll = true;
                    }

                    return startScroll && super.onInterceptTouchEvent(e);
                }
                return super.onInterceptTouchEvent(e);
            case ACTION_POINTER_DOWN:
                this.mScrollPointerId = e.getPointerId(actionIndex);
                this.mInitialTouchX = (int) (e.getX(actionIndex) + 0.5F);
                this.mInitialTouchY = (int) (e.getY(actionIndex) + 0.5F);
                return super.onInterceptTouchEvent(e);
        }
        return super.onInterceptTouchEvent(e);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int touchY = (int) e.getY();
        switch (e.getAction()) {
            case ACTION_DOWN:
                touchDownPointY = touchY;
                downYCanDoRefresh = canViewPositionRefresh();
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchDownPointY == -1) {
                    touchDownPointY = touchY;
                }
                if (refreshEnable && downYCanDoRefresh && isFingerDragging()) {
                    //进行下拉刷新
                    int diffY = touchY - touchDownPointY;
                    if (diffY > 0 && refreshState == RefreshState.NORMAL) {
                        setRefreshState(RefreshState.PULL_TO_REFRESH);
                        if (refreshView != null) {
                            refreshView.onRefreshStart();
                        }
                    }

                    if (refreshState == RefreshState.PULL_TO_REFRESH) {
                        fingerMove(touchY - touchDownPointY);
                    }
                }

                break;
            default:
                if (refreshState == RefreshState.PULL_TO_REFRESH) {
                    fingerRelease(touchY - touchDownPointY);
                } else if (refreshState == RefreshState.REFRESHING) {

                } else {
                    setRefreshState(RefreshState.NORMAL);
                }
                touchDownPointY = -1;
                break;
        }
        return super.onTouchEvent(e);
    }


    /***
     * 当前的是否可以进行下拉刷新
     */
    public boolean canViewPositionRefresh() {
        if (refreshContentView == null) {
            return false;
        }
        final Adapter adapter = getAdapter();
        if (adapter == null || adapter.getItemCount() <= 0) {
            return true;
        }
        View firstChild = getChildAt(0);
        int position = getChildLayoutPosition(firstChild);
        if (position == 0) {
            return firstChild.getTop() == refreshContentView.getTop();
        }
        return false;
    }

    private boolean isFingerDragging() {
        return getScrollState() == SCROLL_STATE_DRAGGING;
    }

    /***
     * 手指在屏幕上滑动
     */
    private void fingerMove(int slidY) {
        if (slidY < 0) {
            slidY = 0;
        }
        int maxHeight = refreshView.getMaxHeight();
        double sinY = Math.sin(Math.min(Math.PI * slidY / (maxHeight * 3), Math.PI / 2));
        refreshView.onPullProgress((int) (sinY * 100));
        int finalSlidY = (int) (sinY * maxHeight);
        setRefreshContentHeight(finalSlidY);
    }

    public void setShowRefreshContentPercent(@IntRange(from = 0,to = 100) int value) {
        if (refreshView == null) {
            return;
        }
        refreshView.setShowRefreshContentPercent(value);
    }

    public void setDefaultRefreshContentHeight(int value) {
        if (refreshView == null) {
            return;
        }
        refreshView.setContentHeight(value);
    }

    public void setDefaultRefreshMaxHeight(int value) {
        if (refreshView == null) {
            return;
        }
        refreshView.setMaxHeight(value);
    }

    /***
     * 手指从屏幕上松开，判断是否需要进行下拉刷新
     */
    private void fingerRelease(int slidY) {
        //判断是需要下拉刷新还是恢复到初始状态
        int refreshHeight = refreshView.getContentHeight();
        if (slidY >= refreshHeight) {
            setRefreshState(RefreshState.REFRESHING);
            changeToRefreshAnim();
        } else {
            setRefreshState(RefreshState.NORMAL);
            changeToNormalAnim();
        }
    }

    /***
     * 设置下拉刷新容器高度
     */
    private void setRefreshContentHeight(int height) {
        if (refreshContentView == null || refreshContentView.getHeight() == height) {
            return;
        }
        LayoutParams params = (LayoutParams) refreshContentView.getLayoutParams();
        params.height = height;
        refreshContentView.setLayoutParams(params);
    }

    private void changeToRefreshAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(refreshContentView.getHeight(), refreshView.getContentHeight());
        valueAnimator.setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                if (refreshView != null) {
                    refreshView.onPullProgress(value * 100 / refreshView.getContentHeight());
                }
                setRefreshContentHeight(value);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (refreshListener != null) {
                    refreshListener.beginRefresh();
                }
                if (refreshView != null) {
                    refreshView.onRefreshing();
                }
            }
        });
        valueAnimator.start();
    }

    private void changeToNormalAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(refreshContentView.getHeight(), 0);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                if (refreshView != null) {
                    refreshView.onPullProgress(value * 100 / refreshView.getContentHeight());
                }
                setRefreshContentHeight(value);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setRefreshState(RefreshState.NORMAL);
            }
        });
        valueAnimator.start();
    }

    private void changeToRefreshDone() {
        if (!refreshView.isRefreshing()) {
            return;
        }
        refreshView.onRefreshDone();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(refreshView.getHoldTime());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeToNormalAnim();
            }
        });
        valueAnimator.start();
    }

    private void showLoadMoreContent() {
        LayoutParams params = (LayoutParams) loadMoreContentLayout.getLayoutParams();
        params.height = LayoutParams.WRAP_CONTENT;
        loadMoreContentLayout.setLayoutParams(params);
    }

    private void hideLoadMoreContent() {
        LayoutParams params = (LayoutParams) loadMoreContentLayout.getLayoutParams();
        params.height = 0;
        loadMoreContentLayout.setLayoutParams(params);
    }

    public void endRefresh() {
        changeToRefreshDone();
    }

    public void showLoadingMore() {
        showLoadMoreContent();
        loadMore.changeToLoading();
    }

    public void showLoadMoreError() {
        showLoadMoreContent();
        loadMore.changeToLoadError();
    }

    public void showLoadMoreEnd() {
        showLoadMoreContent();
        loadMore.changeToDataEnd();
    }

    public void hideLoadMore() {
        hideLoadMoreContent();
    }

    public void setRefreshEnable(boolean flag) {
        refreshEnable = flag;
    }

    public boolean isRefreshEnable() {
        return refreshEnable;
    }

    private void setRefreshState(RefreshState refreshState) {
        this.refreshState = refreshState;
    }

    public void setPullToRefreshListener(PullToRefreshListener listener) {
        this.refreshListener = listener;
    }

    @Override
    public void setScrollingTouchSlop(int slopConstant) {
        ViewConfiguration vc = ViewConfiguration.get(this.getContext());
        switch (slopConstant) {
            case TOUCH_SLOP_DEFAULT:
                this.mTouchSlop = vc.getScaledTouchSlop();
                break;
            case TOUCH_SLOP_PAGING:
                this.mTouchSlop = vc.getScaledPagingTouchSlop();
                break;
            default:
        }
        super.setScrollingTouchSlop(slopConstant);
    }


    private enum RefreshState {
        PULL_TO_REFRESH,
        NORMAL,
        REFRESHING,
    }

    public interface PullToRefreshListener {
        void beginRefresh();
    }
}
