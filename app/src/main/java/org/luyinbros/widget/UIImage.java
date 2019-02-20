package org.luyinbros.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

//增加比例
public class UIImage extends AppCompatImageView {

    public UIImage(Context context) {
        this(context, null);
    }

    public UIImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
