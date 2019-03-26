package org.luyinbros.widget.refresh;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;


public class DefaultLoadMoreView extends LinearLayoutCompat {
    private AppCompatTextView statusTextView;
    private AppCompatImageView refreshImageView;
    private int mStatus = -1;


    public DefaultLoadMoreView(Context context) {
        super(context);

        statusTextView = new AppCompatTextView(getContext());
        statusTextView.setTextSize(14);
        statusTextView.setTextColor(0xFF999999);

        refreshImageView = new AppCompatImageView(getContext());

        addView(refreshImageView);
        addView(statusTextView);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px(44)));
        setGravity(Gravity.CENTER);
        setPadding(0, px(10), 0, px(10));
        setOrientation(HORIZONTAL);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
        if (mStatus == LoadMoreRefreshController.LOAD_MORE_STATUS_IDLE) {
            statusTextView.setText("上拉加载更多");
            statusTextView.setVisibility(VISIBLE);
            refreshImageView.setVisibility(View.GONE);
        } else if (mStatus == LoadMoreRefreshController.LOAD_MORE_STATUS_REFRESH) {
            statusTextView.setText("加载中");
            statusTextView.setVisibility(VISIBLE);
            refreshImageView.setVisibility(View.VISIBLE);
        } else if (mStatus == LoadMoreRefreshController.LOAD_MORE_STATUS_NO_MORE) {
            statusTextView.setText("底线在此，不能更低了呦~");
            statusTextView.setVisibility(VISIBLE);
            refreshImageView.setVisibility(View.GONE);
        } else if (mStatus == LoadMoreRefreshController.LOAD_MORE_STATUS_FAILURE) {
            statusTextView.setText("上拉加载错误，点击重新刷新");
            statusTextView.setVisibility(VISIBLE);
            refreshImageView.setVisibility(View.GONE);
        } else if (mStatus == LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID) {
            statusTextView.setVisibility(GONE);
            refreshImageView.setVisibility(View.GONE);
        }

    }

    private int px(final float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
