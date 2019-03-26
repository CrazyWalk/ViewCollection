package org.luyinbros.widget.refresh.update;

import android.support.v7.widget.RecyclerView;

import org.luyinbros.widget.recyclerview.RecyclerViewCell;
import org.luyinbros.widget.refresh.RxRefreshListDelegate;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleRefreshListCell<T, VH extends RecyclerView.ViewHolder> extends RecyclerViewCell<VH> implements SingleRefreshListUpdater<T> {
    private List<T> list;
    private RxRefreshListDelegate refreshListDelegate;
    private int mPage = 0;
    private final static int INIT_PAGE = 0;
    private int pageSize = 10;

    public SingleRefreshListCell(RxRefreshListDelegate refreshListDelegate) {
        this.refreshListDelegate = refreshListDelegate;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public void updatePageRefreshSuccess(List<T> data) {
        mPage = INIT_PAGE;
        this.list = new ArrayList<>();
        this.list.addAll(data);
        refreshListDelegate.notifyPageRefreshSuccess(hasMore(data));
    }

    @Override
    public void notifyPageRefreshFailure() {
        refreshListDelegate.showFailurePage();
    }

    @Override
    public void updatePullDownRefreshSuccess(List<T> data) {
        mPage = INIT_PAGE;
        this.list = new ArrayList<>();
        this.list.addAll(data);
        refreshListDelegate.notifyPullDownRefreshSuccess(hasMore(data));
    }

    @Override
    public void notifyPullDownRefreshFailure() {
        refreshListDelegate.notifyPullDownRefreshFailure();
    }

    @Override
    public void updatePullUpRefreshSuccess(List<T> data) {
        if (data == null || data.size() == 0) {
            refreshListDelegate.notifyPullUpRefreshSuccess(false);
        } else {
            int currentSize = getItemCount();
            mPage++;
            list.addAll(data);
            notifyItemRangeInserted(currentSize, data.size());
            refreshListDelegate.notifyPullUpRefreshSuccess(hasMore(data));
        }
    }

    @Override
    public void notifyPullUpRefreshFailure() {
        refreshListDelegate.notifyPullUpRefreshFailure();
    }

    @Override
    public int getInitPage() {
        return INIT_PAGE;
    }

    @Override
    public T getDataItem(int position) {
        return list.get(position);
    }

    @Override
    public int getNextPage() {
        return mPage + 1;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getDataSize() {
        return getItemCount();
    }

    private boolean hasMore(List<T> data) {
        return data != null && data.size() == pageSize;
    }


}
