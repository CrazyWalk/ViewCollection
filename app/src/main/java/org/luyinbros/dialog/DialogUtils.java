package org.luyinbros.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class DialogUtils {
    @Nullable
    public static FragmentActivity getDialogContext(@Nullable Context context) {
        if (context != null) {
            if (context instanceof Activity) {
                if (context instanceof FragmentActivity) {
                    return (FragmentActivity) context;
                } else {
                    return null;
                }
            } else if (context instanceof ContextWrapper) {
                return getDialogContext(((ContextWrapper) context).getBaseContext());
            } else {
                return null;
            }
        }
        return null;
    }

    @Nullable
    public static FragmentActivity getDialogContext(@Nullable Fragment fragment) {
        if (fragment != null) {
            Context context = fragment.getContext();
            if (context != null) {
                return getDialogContext(context);
            }
        }
        return null;
    }

    @Nullable
    public static FragmentManager getFragmentManager(@Nullable Fragment fragment) {
        if (fragment != null && fragment.getContext() != null) {
            return fragment.getChildFragmentManager();
        }
        return null;
    }

    @Nullable
    public static FragmentManager getFragmentManager(@Nullable Context context) {
        FragmentActivity fragmentActivity = getDialogContext(context);
        if (fragmentActivity != null) {
            return ((FragmentActivity) context).getSupportFragmentManager();
        }
        return null;
    }
}
