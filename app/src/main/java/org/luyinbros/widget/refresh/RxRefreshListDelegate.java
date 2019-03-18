package org.luyinbros.widget.refresh;

import org.luyinbros.widget.status.StatusLayoutController;

import io.reactivex.disposables.Disposable;

public interface RxRefreshListDelegate extends RefreshListDelegate {

    void setRefreshObserver(RefreshObserver observer);

    /**
     * 调用此方法无效
     */
    void setOnLoadMoreRefreshListener(LoadMoreRefreshController.OnLoadMoreRefreshListener listener);

    /**
     * 调用此方法无效
     */
    void setOnPullDownRefreshListener(RefreshLayoutController.OnPullDownRefreshListener listener);

    /**
     * 调用此方法无效
     */
    void setOnPageRefreshListener(StatusLayoutController.OnPageRefreshListener onPageRefreshListener);



    interface RefreshObserver {

        Disposable onPageRefresh();

        Disposable onPullDownRefresh();

        Disposable onLoadMoreRefresh();

    }

}
