package org.luyinbros.widget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.luyinbros.widget.R;

public class FlowLayout extends ViewGroup {
    private int mColumnMargin = 0;
    private int mRowMargin = 0;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        setColumnMargin(a.getDimensionPixelOffset(R.styleable.FlowLayout_flowL_columnMargin, 0));
        setRowMargin(a.getDimensionPixelOffset(R.styleable.FlowLayout_flowL_rowMargin, 0));
        a.recycle();
    }

    public void setColumnMargin(int margin) {
        this.mColumnMargin = margin;
        requestLayout();
    }

    public void setRowMargin(int margin) {
        this.mRowMargin = margin;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            setMeasuredDimension(getZeroMeasuredDimension(widthMeasureSpec), getZeroMeasuredDimension(heightMeasureSpec));
        } else {
            final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            View child;
            LayoutParams lp;

            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                lp = (LayoutParams) child.getLayoutParams();

                child.measure(
                        getChildMeasureSpec(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), getPaddingLeft() + getPaddingRight(), lp.width),
                        getChildMeasureSpec(heightMeasureSpec, 0, lp.height));
            }

            int totalHeight = getPaddingTop() + getPaddingBottom();
            final int maxItemWidth = parentWidth - getPaddingLeft() - getPaddingRight();
            int currentRemainWidth = maxItemWidth;
            int currentColumnMaxHeight = 0;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                if (child.getMeasuredWidth() <= currentRemainWidth) {
                    currentRemainWidth -= child.getMeasuredWidth() + mColumnMargin;
                    currentColumnMaxHeight = Math.max(currentColumnMaxHeight, getChildAt(i).getMeasuredHeight());
                    if (i == childCount - 1) {
                        totalHeight += currentColumnMaxHeight;
                    }
                } else {
                    totalHeight += currentColumnMaxHeight;
                    currentColumnMaxHeight = 0;
                    if (i < childCount - 1) {
                        totalHeight += mRowMargin;
                    }
                    currentRemainWidth = maxItemWidth;
                    i--;
                }
            }
            setMeasuredDimension(parentWidth, totalHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        if (childCount != 0) {
            int top = getPaddingTop();
            int left = getPaddingLeft();
            final int maxItemWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int currentRemainWidth = maxItemWidth;
            int currentColumnMaxHeight = 0;
            View child;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                if (child.getMeasuredWidth() <= currentRemainWidth) {
                    currentRemainWidth -= child.getMeasuredWidth() + mColumnMargin;
                    child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                    left += child.getMeasuredWidth() + mColumnMargin;
                    currentColumnMaxHeight = Math.max(currentColumnMaxHeight, getChildAt(i).getMeasuredHeight());
                } else {
                    left = getPaddingLeft();
                    top += currentColumnMaxHeight + mRowMargin;
                    currentRemainWidth = maxItemWidth;
                    i--;
                }
            }
        }
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


    public static class LayoutParams extends ViewGroup.LayoutParams {

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
