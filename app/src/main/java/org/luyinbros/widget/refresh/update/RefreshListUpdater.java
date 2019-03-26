package org.luyinbros.widget.refresh.update;

public interface RefreshListUpdater<PageRefreshData, PullDownData, PullUpData> {

    void updatePageRefreshSuccess(PageRefreshData data);

    void notifyPageRefreshFailure();

    void updatePullDownRefreshSuccess(PullDownData data);

    void notifyPullDownRefreshFailure();

    void updatePullUpRefreshSuccess(PullUpData data);

    void notifyPullUpRefreshFailure();

    int getInitPage();

    int getNextPage();

    int getPageSize();
}
