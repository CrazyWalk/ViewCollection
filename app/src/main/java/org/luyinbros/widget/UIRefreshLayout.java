package org.luyinbros.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class UIRefreshLayout extends SwipeRefreshLayout {

    public UIRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public UIRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
