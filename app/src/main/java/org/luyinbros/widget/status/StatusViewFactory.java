package org.luyinbros.widget.status;

import android.content.Context;

public class StatusViewFactory {

    public static StatusLayoutController.StatusPage defaultEmptyPage(Context context) {
        return new SampleEmptyStatusPage(context);
    }

    public static StatusLayoutController.StatusPage defaultFailurePage(Context context) {
        return new SampleFailureStatusPage(context);
    }

    public static StatusLayoutController.StatusPage defaultRefreshPage(Context context) {
        return new SampleRefreshStatusPage(context);
    }
}
