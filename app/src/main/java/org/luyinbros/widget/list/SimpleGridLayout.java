package org.luyinbros.widget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.luyinbros.widget.R;

public class SimpleGridLayout extends ViewGroup {
    private int columnCount = 3;
    private int mColumnMargin = 0;
    private int mRowMargin = 0;

    public SimpleGridLayout(Context context) {
        this(context, null);
    }

    public SimpleGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleGridLayout);
        setColumnCount(a.getInt(R.styleable.SimpleGridLayout_sgl_column, 3));
        setColumnMargin(a.getDimensionPixelOffset(R.styleable.SimpleGridLayout_sgl_columnMargin, 0));
        setRowMargin(a.getDimensionPixelOffset(R.styleable.SimpleGridLayout_sgl_rowMargin, 0));
        a.recycle();
    }


    public void setColumnCount(int columnCount) {
        if (columnCount > 0) {
            this.columnCount = columnCount;
            requestLayout();
        }
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
            final int itemWidth = (parentWidth - (columnCount - 1) * mColumnMargin - getPaddingLeft() - getPaddingRight()) / columnCount;
            View child;
            LayoutParams lp;

            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                lp = (LayoutParams) child.getLayoutParams();
                measureChild(child,
                        getChildMeasureSpec(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), 0, lp.width),
                        getChildMeasureSpec(heightMeasureSpec, 0, lp.height));
            }
            final int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
            switch (parentHeightMode) {
                case MeasureSpec.EXACTLY:
                    setMeasuredDimension(parentWidth, MeasureSpec.getSize(heightMeasureSpec));
                    break;
                case MeasureSpec.AT_MOST:
                case MeasureSpec.UNSPECIFIED:
                    boolean isColumnEnd;
                    int itemHeight;
                    int startIndex = 0;
                    int totalHeight = getPaddingTop() + getPaddingBottom();
                    int totalRow = 0;
                    for (int i = 0; i < childCount; i++) {
                        isColumnEnd = i % columnCount == columnCount - 1 ||
                                i == childCount - 1;
                        if (isColumnEnd) {
                            itemHeight = 0;
                            totalRow++;
                            for (int j = startIndex; j <= i; j++) {
                                child = getChildAt(j);
                                itemHeight = Math.max(itemHeight, child.getMeasuredHeight());
                            }
                            totalHeight += itemHeight;
                        }
                    }
                    setMeasuredDimension(parentWidth, totalHeight + (totalRow - 1) * mRowMargin);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        if (childCount != 0) {
            boolean isColumnEnd = false;
            int top = getPaddingTop();
            int startLeft = getPaddingLeft();
            int itemHeight = 0;
            View child;
            for (int i = 0; i < childCount; i++) {
                if (isColumnEnd) {
                    startLeft = getPaddingLeft();
                    top += itemHeight + mRowMargin;
                    itemHeight = 0;
                }
                child = getChildAt(i);
                child.layout(startLeft,
                        top,
                        startLeft + child.getMeasuredWidth(),
                        top + child.getMeasuredHeight());
                startLeft += (child.getMeasuredWidth() + mColumnMargin);
                itemHeight = Math.max(itemHeight, child.getMeasuredHeight());
                isColumnEnd = i % columnCount == columnCount - 1;
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
