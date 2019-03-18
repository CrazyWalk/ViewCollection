package org.luyinbros.widget.status;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class DefaultStatusLayoutController implements StatusLayoutController {
    private StatusPageProvider mProvider;
    private ViewGroup mParent;
    private View mContentView;
    private int indexInParent = -1;
    private View mCurrentView;
    private int mStatus;
    private OnPageRefreshListener onPageRefreshListener;

    public DefaultStatusLayoutController(@NonNull View contentView) {
        mProvider = new DefaultStatusPageProvider();
        this.mStatus = STATUS_PAGE_CONTENT_VIEW;
        this.mCurrentView = contentView;
        this.mContentView = contentView;
        ViewParent parent = mCurrentView.getParent();
        if (parent instanceof ViewGroup) {
            mParent = (ViewGroup) parent;
            indexInParent = mParent.indexOfChild(mCurrentView);
        } else {
            mParent = null;
            indexInParent = -1;
        }
    }

    @Override
    public void addStatusPage(StatusPage statusView) {
        mProvider.addStatusPage(statusView);
    }

    @Override
    public void registerStatusPageCreator(StatusPageCreator statusViewCreator) {
        mProvider.registerStatusPageCreator(statusViewCreator);
    }

    @Override
    public void showEmptyPage() {
        if (mProvider.hasStatus(STATUS_PAGE_EMPTY)) {
            showStatusPage(STATUS_PAGE_EMPTY);
        }
    }

    @Override
    public void showFailurePage() {
        if (mProvider.hasStatus(STATUS_PAGE_FAILURE)) {
            showStatusPage(STATUS_PAGE_FAILURE);
        }
    }

    @Override
    public void showRefreshPage() {
        if (mProvider.hasStatus(STATUS_PAGE_REFRESH)) {
            showStatusPage(STATUS_PAGE_REFRESH);
            if (onPageRefreshListener != null) {
                onPageRefreshListener.onPageRefresh();
                ;
            }
        }
    }

    @Override
    public void showStatusPage(int status) {
        if (mStatus == status || status < 0) {
            return;
        }
        findParentViewInIndex();
        if (mParent != null &&
                indexInParent != -1 &&
                mProvider.hasStatus(status)) {
            mParent.removeView(mCurrentView);
            StatusPage statusPage = mProvider.getOrCreateStatusPage(status);
            if (statusPage != null) {
                mStatus = status;
                mCurrentView = (View) statusPage;
                mParent.addView(mCurrentView, indexInParent,mContentView.getLayoutParams());
            }

        }


    }

    @Override
    public void showContentView() {
        if (mStatus == STATUS_PAGE_CONTENT_VIEW) {
            return;
        }
        findParentViewInIndex();
        if (mParent != null &&
                indexInParent != -1) {
            mParent.removeView(mCurrentView);
            mStatus = STATUS_PAGE_CONTENT_VIEW;
            mCurrentView = mContentView;
            mParent.addView(mContentView, indexInParent);
        }
    }

    @Override
    public int getPageStatus() {
        return mStatus;
    }

    @Override
    public void setOnPageRefreshListener(OnPageRefreshListener onPageRefreshListener) {
        this.onPageRefreshListener = onPageRefreshListener;
    }

    private void findParentViewInIndex() {
        ViewParent parent = mCurrentView.getParent();

        if (parent instanceof ViewGroup) {
            mParent = (ViewGroup) parent;
            indexInParent = mParent.indexOfChild(mCurrentView);
        } else {
            mParent = null;
            indexInParent = -1;
        }
    }
}
