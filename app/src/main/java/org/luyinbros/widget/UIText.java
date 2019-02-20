package org.luyinbros.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class UIText extends AppCompatTextView {

    public UIText(Context context) {
        super(context, null);
        init();
    }

    public UIText(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public UIText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setIncludeFontPadding(false);
    }


}
