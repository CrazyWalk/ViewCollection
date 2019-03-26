package org.luyinbros.widget.refresh;

public interface RefreshLayoutController {

    void setOnPullDownRefreshListener(OnPullDownRefreshListener listener);

    void notifyPullDownRefresh();

    void notifyPullDownRefreshComplete();

    boolean isPullDownRefreshing();

    void setPullDownRefreshEnable(boolean isEnable);


}
