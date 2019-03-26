package org.luyinbros.widget.status;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

public interface StatusLayoutController {
    int STATUS_PAGE_CONTENT_VIEW = -1;
    int STATUS_PAGE_EMPTY = 0;
    int STATUS_PAGE_FAILURE = 1;
    int STATUS_PAGE_REFRESH = 2;

    void addStatusPage(StatusPage statusView);

    void registerStatusPageCreator(StatusPageCreator statusViewCreator);

    void showEmptyPage();

    void showFailurePage();

    void showRefreshPage();

    void showStatusPage(int status);

    void showContentView();

    void setOnPageRefreshListener(OnPageRefreshListener onPageRefreshListener);

    int getPageStatus();


    interface StatusPage {
        @IntRange(from = 0)
        int getStatus();
    }

    interface StatusPageCreator {
        @IntRange(from = 0)
        int getStatus();

        @NonNull
        StatusPage onStatusPageCreate();
    }

}
