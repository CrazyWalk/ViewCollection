package org.luyinbros.widget.refresh;

public interface LoadMoreRefreshController {
    int LOAD_MORE_STATUS_INVALID = -1;
    int LOAD_MORE_STATUS_FAILURE = 0;
    int LOAD_MORE_STATUS_NO_MORE = 1;
    int LOAD_MORE_STATUS_IDLE = 2;
    int LOAD_MORE_STATUS_REFRESH = 3;

    void setOnLoadMoreRefreshListener(OnLoadMoreRefreshListener listener);

    int getLoadMoreStatus();

    void setLoadMoreStatus(int status);

    void setLoadMoreEnable(boolean enable);


    boolean isLoadMoreEnable();

    interface OnLoadMoreRefreshListener {
        void onLoadMoreRefresh();
    }
}
