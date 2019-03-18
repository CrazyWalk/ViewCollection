package org.luyinbros.widget.refresh;

import org.luyinbros.widget.status.StatusLayoutController;

public interface RefreshListDelegate {

    void setOnLoadMoreRefreshListener(LoadMoreRefreshController.OnLoadMoreRefreshListener listener);

    void setOnPullDownRefreshListener(RefreshLayoutController.OnPullDownRefreshListener listener);

    void setOnPageRefreshListener(StatusLayoutController.OnPageRefreshListener onPageRefreshListener);

    void openLoadMore();

    void addStatusPage(StatusLayoutController.StatusPage statusPage);

    boolean isRefreshing();

    void notifyPageRefresh();

    void notifyPageRefreshSuccess();

    void notifyPageRefreshSuccess(boolean isLoadMore);

    void showFailurePage();

    void showEmptyPage();

    void showStatusPagePageStatus(int status);

    void notifyPullDownRefresh();

    void notifyPullDownRefreshSuccess();

    void notifyPullDownRefreshSuccess(boolean hasMore);

    void notifyPullDownRefreshFailure();

    void notifyLoadMoreRefreshSuccess(boolean hasMore);

    void notifyLoadMoreRefreshFailure();


}
