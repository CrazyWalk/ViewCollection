package org.luyinbros.widget.refresh;

import android.support.annotation.NonNull;

import org.luyinbros.rx.CombineDisposable;
import org.luyinbros.widget.status.OnPageRefreshListener;
import org.luyinbros.widget.status.StatusLayoutController;

import io.reactivex.disposables.Disposable;

public class RxRefreshListDelegate {
    private RefreshListController mRefreshListController;
    private CombineDisposable mDisposable = new CombineDisposable();
    private RefreshObservable mRefreshObservable;
    private int tempLoadMoreRefresh = LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID;
    private boolean isAllowLoadMore = false;

    public RxRefreshListDelegate(@NonNull RefreshListController refreshListController) {
        this.mRefreshListController = refreshListController;
        mRefreshListController.setOnPullDownRefreshListener(new OnPullDownRefreshListener() {
            @Override
            public void onPullDownRefresh() {
                if (mRefreshListController.getLoadMoreStatus() == LoadMoreRefreshController.LOAD_MORE_STATUS_REFRESH) {
                    mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID);
                    tempLoadMoreRefresh = LoadMoreRefreshController.LOAD_MORE_STATUS_IDLE;
                    mDisposable.dispose("pullUp");
                } else {
                    tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
                }
                mDisposable.addDisposable("pullDown", mRefreshObservable.onPullDownRefresh());

            }
        });
    }

    public void setRefreshObservable(RefreshObservable mRefreshObservable) {
        this.mRefreshObservable = mRefreshObservable;
    }

    public void setAllowLoadMore(boolean isAllowLoadMore) {
        this.isAllowLoadMore = isAllowLoadMore;
        if (isAllowLoadMore) {
            mRefreshListController.setOnLoadMoreRefreshListener(new OnLoadMoreRefreshListener() {
                @Override
                public void onLoadMoreRefresh() {
                    if (mRefreshObservable != null) {
                        mDisposable.addDisposable("pullUp", mRefreshObservable.onPullUpRefresh());
                    }

                }
            });
        }

    }

    public boolean isRefreshing() {
        return mRefreshListController.isPullDownRefreshing() || mRefreshListController.isLoadMoreRefreshing();
    }

    public void addStatusPage(@NonNull StatusLayoutController.StatusPage statusPage) {
        if (statusPage.getStatus() == StatusLayoutController.STATUS_PAGE_REFRESH) {
            mRefreshListController.setOnPageRefreshListener(new OnPageRefreshListener() {
                @Override
                public void onPageRefresh() {
                    if (mRefreshObservable != null) {
                        mRefreshListController.setLoadMoreEnable(false);
                        mRefreshListController.setPullDownRefreshEnable(false);
                        mDisposable.dispose();
                        mDisposable.addDisposable("pageRefresh", mRefreshObservable.onPageRefresh());
                    }
                }
            });
        }
        mRefreshListController.addStatusPage(statusPage);
    }


    public void notifyPageRefreshSuccess() {
        mRefreshListController.setPullDownRefreshEnable(true);
        mRefreshListController.notifyPullDownRefreshComplete();
        int dataCount = mRefreshListController.getItemCount();
        if (dataCount == 0) {
            showEmptyPage();
        } else {
            mRefreshListController.showContentView();
        }
        tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
        mDisposable.dispose();
    }

    public void notifyPageRefreshSuccess(boolean isLoadMore) {
        mRefreshListController.setPullDownRefreshEnable(true);
        mRefreshListController.notifyPullDownRefreshComplete();
        int dataCount = mRefreshListController.getItemCount();
        if (dataCount == 0) {
            showEmptyPage();
        } else {
            if (isLoadMoreVisible()) {
                mRefreshListController.setLoadMoreEnable(true);
                if (isLoadMore) {
                    mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_IDLE);
                } else {
                    mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_NO_MORE);
                }
            } else {
                mRefreshListController.setLoadMoreEnable(false);
            }
            mRefreshListController.showContentView();
        }
        mRefreshListController.notifyDataSetChanged();
        tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
        mDisposable.dispose();
    }

    public void showStatusPage(int status) {
        mRefreshListController.notifyPullDownRefreshComplete();
        mRefreshListController.setLoadMoreEnable(false);
        mRefreshListController.setPullDownRefreshEnable(false);
        mRefreshListController.showStatusPage(status);
        tempLoadMoreRefresh = LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID;
        mDisposable.dispose();
    }

    private void showEmptyPage() {
        mRefreshListController.notifyPullDownRefreshComplete();
        mRefreshListController.setLoadMoreEnable(false);
        mRefreshListController.setPullDownRefreshEnable(true);
        mRefreshListController.showEmptyPage();
        tempLoadMoreRefresh = LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID;
        mDisposable.dispose("pageRefresh");
        mDisposable.dispose("pullDown");
    }

    public void showFailurePage() {
        mRefreshListController.notifyPullDownRefreshComplete();
        mRefreshListController.setLoadMoreEnable(false);
        mRefreshListController.setPullDownRefreshEnable(false);
        mRefreshListController.showFailurePage();
        tempLoadMoreRefresh = LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID;
        mDisposable.dispose();
    }

    public void showRefreshPage() {
        mRefreshListController.showRefreshPage();
    }

    public void notifyPullDownRefresh() {
        mRefreshListController.notifyPullDownRefresh();
    }

    /**
     * 下拉加载成功
     */
    public void notifyPullDownRefreshSuccess() {
        mRefreshListController.setPullDownRefreshEnable(true);
        mRefreshListController.notifyPullDownRefreshComplete();
        int dataCount = mRefreshListController.getItemCount();
        if (dataCount == 0) {
            showEmptyPage();
        } else {
            mRefreshListController.setLoadMoreEnable(isAllowLoadMore);
            mRefreshListController.showContentView();
        }
        mRefreshListController.notifyDataSetChanged();
        tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
        mDisposable.dispose();
    }

    /**
     * 下拉加载成功
     */
    public void notifyPullDownRefreshSuccess(boolean isLoadMore) {
        mRefreshListController.setPullDownRefreshEnable(true);
        mRefreshListController.notifyPullDownRefreshComplete();
        int dataCount = mRefreshListController.getItemCount();
        if (dataCount == 0) {
            showEmptyPage();
        } else {
            if (isLoadMoreVisible()) {
                mRefreshListController.setLoadMoreEnable(true);
                if (isLoadMore) {
                    mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_IDLE);
                } else {
                    mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_NO_MORE);
                }
            } else {
                mRefreshListController.setLoadMoreEnable(false);
            }
            mRefreshListController.showContentView();
        }
        mRefreshListController.notifyDataSetChanged();
        tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
        mDisposable.dispose();
    }

    /**
     * 下拉加载失败
     */
    public void notifyPullDownRefreshFailure() {
        mRefreshListController.setPullDownRefreshEnable(true);
        mRefreshListController.notifyPullDownRefreshComplete();
        if (tempLoadMoreRefresh != LoadMoreRefreshController.LOAD_MORE_STATUS_INVALID) {
            mRefreshListController.setLoadMoreStatus(tempLoadMoreRefresh);
        }
        tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
        mDisposable.dispose();
        int dataCount = mRefreshListController.getItemCount();
        if (dataCount == 0) {
            showFailurePage();
        }
    }

    /**
     * 上拉加载成功
     */
    public void notifyPullUpRefreshSuccess(boolean hasMore) {
        if (hasMore) {
            mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_IDLE);
        } else {
            mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_NO_MORE);
        }
        tempLoadMoreRefresh = mRefreshListController.getLoadMoreStatus();
        mDisposable.dispose();

    }

    /**
     * 下拉加载失败
     */
    public void notifyPullUpRefreshFailure() {
        mRefreshListController.setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_FAILURE);
        mDisposable.dispose();
    }

    public void dispose() {
        mDisposable.dispose();
    }

    private boolean isLoadMoreVisible() {
        if (isAllowLoadMore) {
            if (mRefreshObservable != null) {
                return mRefreshObservable.isLoadMoreVisible();
            }
        }
        return false;

    }

    public interface RefreshObservable {

        Disposable onPageRefresh();

        Disposable onPullUpRefresh();

        Disposable onPullDownRefresh();

        boolean isLoadMoreVisible();

    }
}
