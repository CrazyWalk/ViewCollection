package org.luyinbros.widget.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class RefreshLayout extends SwipeRefreshLayout implements RefreshLayoutController {

    private OnPullDownRefreshListener onPullDownRefreshListener;

    public RefreshLayout(@NonNull Context context) {
        super(context);
    }

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnPullDownRefreshListener(final OnPullDownRefreshListener listener) {
        this.onPullDownRefreshListener = listener;
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                onPullDownRefreshListener.onPullDownRefresh();
            }
        });
    }

    @Override
    public void notifyPullDownRefresh() {
        setRefreshing(true);
        if (onPullDownRefreshListener != null) {
            onPullDownRefreshListener.onPullDownRefresh();
        }
    }

    @Override
    public void notifyPullDownRefreshComplete() {
        setRefreshing(false);
    }

    @Override
    public boolean isPullDownRefreshing() {
        return isRefreshing();
    }

    @Override
    public void setPullDownRefreshEnable(boolean isEnable) {
        setEnabled(isEnable);
    }
}
