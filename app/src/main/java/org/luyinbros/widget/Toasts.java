package org.luyinbros.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.ref.SoftReference;

public class Toasts {
    private static SoftReference<Toast> toastReference;

    /**
     * 显示toast
     *
     * @param context 指定的上下文
     * @param res     文本资源
     */
    public static void show(@Nullable Context context, int res) {
        if (context != null) {
            Toast toast = getToast(context);
            toast.setText(res);
            toast.show();
        }

    }

    /**
     * 显示toast
     *
     * @param context 指定的上下文
     * @param text    文本
     */
    public static void show(@Nullable Context context, CharSequence text) {
        if (context != null && !TextUtils.isEmpty(text)) {
            Toast toast = getToast(context);
            toast.setText(text);
            toast.show();
        }
    }

    private static Toast getToast(@NonNull Context context) {
        if (toastReference == null || toastReference.get() == null) {
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toastReference = new SoftReference<>(toast);
        }
        return toastReference.get();
    }
}
