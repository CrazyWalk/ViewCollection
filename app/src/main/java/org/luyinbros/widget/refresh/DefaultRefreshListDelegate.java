package org.luyinbros.widget.refresh;

import org.luyinbros.widget.status.StatusLayoutController;

public class DefaultRefreshListDelegate implements RefreshListDelegate {
    private RefreshListController mRefreshListController;

    public DefaultRefreshListDelegate(RefreshListController refreshListController) {
        this.mRefreshListController = refreshListController;
    }

    @Override
    public void setOnLoadMoreRefreshListener(LoadMoreRefreshController.OnLoadMoreRefreshListener listener) {
        ;
    }

    @Override
    public void setOnPullDownRefreshListener(RefreshLayoutController.OnPullDownRefreshListener listener) {

    }

    @Override
    public void setOnPageRefreshListener(StatusLayoutController.OnPageRefreshListener onPageRefreshListener) {

    }

    @Override
    public void openLoadMore() {

    }

    @Override
    public void addStatusPage(StatusLayoutController.StatusPage statusPage) {

    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public void notifyPageRefresh() {

    }

    @Override
    public void notifyPageRefreshSuccess() {

    }

    @Override
    public void notifyPageRefreshSuccess(boolean isLoadMore) {

    }

    @Override
    public void showFailurePage() {

    }

    @Override
    public void showEmptyPage() {

    }

    @Override
    public void showStatusPagePageStatus(int status) {

    }

    @Override
    public void notifyPullDownRefresh() {

    }

    @Override
    public void notifyPullDownRefreshSuccess() {

    }

    @Override
    public void notifyPullDownRefreshSuccess(boolean hasMore) {

    }

    @Override
    public void notifyPullDownRefreshFailure() {

    }

    @Override
    public void notifyLoadMoreRefreshSuccess(boolean hasMore) {

    }

    @Override
    public void notifyLoadMoreRefreshFailure() {

    }
}
