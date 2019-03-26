package org.luyinbros.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class DialogFactory {
    public static final String LOADING_DIALOG_NAME = "LoadingDialog";

    public static void showDialog(@Nullable DialogFragment dialogFragment,
                                  @Nullable FragmentManager fragmentManager,
                                  @Nullable Bundle bundle,
                                  @NonNull String name) {
        if (fragmentManager != null) {
            DialogFragment currentDialog = (DialogFragment) fragmentManager.findFragmentByTag(name);
            if (currentDialog != null) {
                currentDialog.dismissAllowingStateLoss();
            }
            if (dialogFragment != null) {
                dialogFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(dialogFragment, name);
                transaction.commitNowAllowingStateLoss();
                transaction.show(dialogFragment);
            }

        }

    }
}
