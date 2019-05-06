package org.luyinbros.utils;

import android.content.Context;
import android.support.annotation.NonNull;

public class Dimens {
    public static int px(@NonNull Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
