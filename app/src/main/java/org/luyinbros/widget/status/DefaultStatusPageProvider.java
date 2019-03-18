package org.luyinbros.widget.status;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

public class DefaultStatusPageProvider implements StatusPageProvider {
    private SparseArray<StatusLayoutController.StatusPageCreator> mCreatorMap = new SparseArray<>();

    @Override
    public void addStatusPage(@NonNull final StatusLayoutController.StatusPage statusPage) {
        final int currentStatus = statusPage.getStatus();
        if (!hasStatus(currentStatus)) {
            registerStatusPageCreator(new StatusLayoutController.StatusPageCreator() {

                @Override
                public int getStatus() {
                    return currentStatus;
                }

                @NonNull
                @Override
                public StatusLayoutController.StatusPage onStatusPageCreate() {
                    return statusPage;
                }
            });
        }
    }

    @Override
    public void registerStatusPageCreator(@NonNull StatusLayoutController.StatusPageCreator statusPageCreator) {
        final int currentStatus = statusPageCreator.getStatus();
        if (!hasStatus(currentStatus)) {
            mCreatorMap.put(currentStatus, statusPageCreator);
        }
    }

    @Nullable
    @Override
    public StatusLayoutController.StatusPage getOrCreateStatusPage(int status) {
        if (hasStatus(status)) {
            return mCreatorMap.get(status).onStatusPageCreate();
        }
        return null;
    }

    @Override
    public boolean hasStatus(int status) {
        return mCreatorMap.get(status) != null;
    }


}
