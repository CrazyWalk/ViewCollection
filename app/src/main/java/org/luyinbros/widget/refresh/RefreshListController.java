package org.luyinbros.widget.refresh;

import org.luyinbros.widget.status.StatusLayoutController;

public interface RefreshListController extends RefreshLayoutController, StatusLayoutController, LoadMoreRefreshController {
    int getItemCount();

    void notifyDataSetChanged();
}
