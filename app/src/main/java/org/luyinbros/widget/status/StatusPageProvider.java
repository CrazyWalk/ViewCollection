package org.luyinbros.widget.status;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface StatusPageProvider {

    void addStatusPage(@NonNull StatusLayoutController.StatusPage statusView);

    void registerStatusPageCreator(@NonNull StatusLayoutController.StatusPageCreator statusPageCreator);

    @Nullable
    StatusLayoutController.StatusPage getOrCreateStatusPage(int status);

    boolean hasStatus(int status);
}
