package org.luyinbros.widget.refresh.update;

import java.util.List;

public interface SingleRefreshListUpdater<T> extends RefreshListUpdater<List<T>, List<T>, List<T>> {

    T getDataItem(int position);

    int getDataSize();
}
