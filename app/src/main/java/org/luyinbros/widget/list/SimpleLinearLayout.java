package org.luyinbros.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class SimpleLinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int orientation = VERTICAL;


    public SimpleLinearLayout(Context context) {
        super(context);
    }

    public SimpleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (orientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        } else {
            return new LayoutParams(p);
        }
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            setMeasuredDimension(getZeroMeasuredDimension(widthMeasureSpec), getZeroMeasuredDimension(heightMeasureSpec));
        } else {

        }
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        //todo
        setMeasuredDimension(getZeroMeasuredDimension(widthMeasureSpec), getZeroMeasuredDimension(heightMeasureSpec));
    }

    private int getZeroMeasuredDimension(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.UNSPECIFIED:
                return 0;
            case MeasureSpec.AT_MOST:
                return 0;
            default:
                return 0;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
