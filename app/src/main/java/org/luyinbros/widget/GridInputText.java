package org.luyinbros.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class GridInputText extends AppCompatEditText {
    private static final String TAG = "GridInputView";
    //文本
    public static final int INPUT_TEXT = 0;
    //数字
    public static final int INPUT_NUMBER = 1;
    //Item是线条
    public static final int ITEM_BG_TYPE_LINE = 1;
    //Item是矩形
    public static final int ITEM_BG_TYPE_RECT = 0;
    //Item自定义
    public static final int ITEM_BG_TYPE_SELF = 2;

    private int mMaxInputLength;
    private Drawable mItemDrawable;
    private boolean mInputHide;
    private int mItemMargin = 0;
    private int mInputType;
    private int mItemBackgroundType;
    private int mItemLineBackgroundWidth;
    private int mItemLineBackgroundColor;
    private boolean mCursorVisible = true;
    private boolean mIncludeParentMargin;
    private CharSequence mText = "";

    private Drawable mNormalDrawable;
    //   private final int[] mFocusState = new int[]{android.R.attr.state_focused};
    private final int[] mNormalState = new int[]{android.R.attr.state_empty};
    private Rect itemRect = new Rect();
    private Paint mRadiusPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private Paint mCursorPaint = new Paint();
    private int itemWidth;
    private int itemHeight;
    private int mCurrentPasswordLength = 0;
    private final int mCursorWidth;
    private boolean isDrawCursor = true;
    private Runnable drawCursorRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentPasswordLength >= mMaxInputLength) {
                isDrawCursor = false;
            } else {
                if (hasFocus()) {
                    isDrawCursor = !isDrawCursor;
                } else {
                    isDrawCursor = false;
                }

            }

            postInvalidate();
            removeCallbacks(drawCursorRunnable);
            postDelayed(drawCursorRunnable, 1000);
        }
    };

    public GridInputText(Context context) {
        this(context, null);
    }

    public GridInputText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridInputText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridInputText);
        setMaxInputLength(a.getInt(R.styleable.GridInputText_gridInputLength, 6));
        setItemBackgroundType(a.getInt(R.styleable.GridInputText_gridInputItemBackgroundType, ITEM_BG_TYPE_RECT));
        Drawable drawable = a.getDrawable(R.styleable.GridInputText_gridInputItemBackground);
        if (drawable != null) {
            setItemDrawable(drawable);
        }
        setInputHide(a.getBoolean(R.styleable.GridInputText_gridInputHide, true));
        setGridInputType(a.getInt(R.styleable.GridInputText_gridInputType, INPUT_NUMBER));
        setItemMargin(a.getDimensionPixelOffset(R.styleable.GridInputText_gridInputItemMargin, 0));
        setItemLineBackgroundWidth(a.getDimensionPixelOffset(R.styleable.GridInputText_gridInputItemLineBackgroundWidth, 0));
        setItemLineBackgroundColor(a.getColor(R.styleable.GridInputText_gridInputItemLineBackgroundColor, getCurrentTextColor()));
        setIncludeParentMargin(a.getBoolean(R.styleable.GridInputText_gridInputIncludeParentMargin, true));
        setGridTextSize(a.getDimensionPixelOffset(R.styleable.GridInputText_gridInputTextSize, (int) getTextSize()));
        setTextColor(getCurrentTextColor());
        a.recycle();
        mRadiusPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mCursorPaint.setAntiAlias(true);
        mCursorWidth = px(1);
        super.setCursorVisible(false);
        setCursorVisible(true);

        setTextColor(getCurrentTextColor());
    }

    public void setMaxInputLength(int maxInputLength) {
        this.mMaxInputLength = maxInputLength;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxInputLength)});
        invalidate();
    }

    public void setItemDrawable(Drawable drawable) {
        if (drawable == null) {
            mNormalDrawable = null;
        } else {
            drawable.setState(mNormalState);
            mNormalDrawable = drawable.getCurrent();
        }
        this.mItemBackgroundType = ITEM_BG_TYPE_SELF;
        this.mItemDrawable = drawable;
        invalidate();
    }

    public void setInputHide(boolean inputHide) {
        this.mInputHide = inputHide;
        invalidate();
    }

    public void setItemMargin(int itemMargin) {
        this.mItemMargin = itemMargin;
        invalidate();
    }

    public void setGridInputType(int inputType) {
        mInputType = inputType;
        if (mInputType == INPUT_NUMBER) {
            super.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                    | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else {
            setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        }
    }

    public void setItemBackgroundType(int type) {
        if (type == ITEM_BG_TYPE_RECT) {
            setItemDrawable(getItemRectDrawable());
        } else if (type == ITEM_BG_TYPE_LINE) {
            setItemDrawable(getItemLineDrawable());
        } else {
            setItemDrawable(null);
        }
        this.mItemBackgroundType = type;
        requestLayout();
    }

    public void setItemLineBackgroundWidth(int itemLineBackgroundWidth) {
        if (mItemBackgroundType == ITEM_BG_TYPE_LINE &&
                mItemDrawable instanceof GradientDrawable) {
            this.mItemLineBackgroundWidth = itemLineBackgroundWidth;
            ((GradientDrawable) mItemDrawable).setStroke(mItemLineBackgroundWidth, mItemLineBackgroundColor);
            requestLayout();
            invalidate();
        }
    }

    public void setItemLineBackgroundColor(int itemLineBackgroundColor) {
        if (mItemBackgroundType == ITEM_BG_TYPE_LINE &&
                mItemDrawable instanceof GradientDrawable) {
            this.mItemLineBackgroundColor = itemLineBackgroundColor;
            ((GradientDrawable) mItemDrawable).setStroke(mItemLineBackgroundWidth, mItemLineBackgroundColor);
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void setCursorVisible(boolean cursorVisible) {
        this.mCursorVisible = cursorVisible;
        if (cursorVisible) {
            showCursorVisible(true);
        } else {
            showCursorVisible(false);
        }
    }

    private void showCursorVisible(boolean isShow) {
        if (isShow && mCursorVisible) {
            isDrawCursor = false;
            postDelayed(drawCursorRunnable, 1000);
        } else {
            removeCallbacks(drawCursorRunnable);
            isDrawCursor = false;
        }
    }

    public void setIncludeParentMargin(boolean includeParentMargin) {
        this.mIncludeParentMargin = includeParentMargin;
        invalidate();
    }

    public void setGridTextSize(float size) {
        mTextPaint.setTextSize(size);
        mCursorPaint.setTextSize(size);
        //super.setTextSize会影响高度
        invalidate();
    }

    @Override
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        mRadiusPaint.setColor(color);
        mCursorPaint.setColor(color);
        invalidate();
        // super.setTextColor(color);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        showCursorVisible(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showCursorVisible(true);
    }

    private int px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    protected Drawable getItemRectDrawable() {
        GradientDrawable defaultDrawable = new GradientDrawable();
        defaultDrawable.setStroke(2, 0xFFE0E0E0);
        defaultDrawable.setColor(0xFFFFFFFF);
        return defaultDrawable;
    }

    private Drawable getItemLineDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.LINE);
        drawable.setColor(getCurrentTextColor());
        return drawable;
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        mCurrentPasswordLength = text.length();
        mText = text;
        invalidate();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            showCursorVisible(true);
        } else {
            showCursorVisible(false);
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        Editable editable = getText();
        if (editable != null) {
            int length = editable.length();
            if (selStart != length || selEnd != length) {
                setSelection(length);
            }
        }

    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNormalDrawable == null) {
            setMeasuredDimension(0, 0);
        } else {
            if (mMaxInputLength <= 0) {
                setMeasuredDimension(0, 0);
            } else {
                final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
                if (mIncludeParentMargin) {
                    itemWidth = (parentWidth - mItemMargin * (mMaxInputLength + 1)) / mMaxInputLength;
                } else {
                    itemWidth = (parentWidth - mItemMargin * (mMaxInputLength - 1)) / mMaxInputLength;
                }

                if (itemWidth <= 0) {
                    itemWidth = 0;
                    itemHeight = 0;
                    setMeasuredDimension(0, 0);
                } else {
                    switch (MeasureSpec.getMode(heightMeasureSpec)) {
                        case MeasureSpec.EXACTLY:
                            itemHeight = MeasureSpec.getSize(heightMeasureSpec);
                            setMeasuredDimension(parentWidth, itemHeight);
                            break;
                        case MeasureSpec.AT_MOST:
                        case MeasureSpec.UNSPECIFIED:
                        default:
                            itemHeight = itemWidth;
                            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), itemHeight);
                            break;

                    }
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mNormalDrawable == null) {
            return;
        }
        if (mMaxInputLength == 0) {
            return;
        }
        itemRect.set(0, 0, 0, itemHeight);
        int startLeft = mIncludeParentMargin ? mItemMargin : 0;
        for (int index = 0; index < mMaxInputLength; index++) {
            itemRect.left = startLeft;
            itemRect.right = itemRect.left + itemWidth;
            if (mItemBackgroundType == ITEM_BG_TYPE_LINE) {
                mNormalDrawable.setBounds(itemRect.left,
                        0,
                        itemRect.right,
                        itemHeight * 2);
            } else {
                mNormalDrawable.setBounds(itemRect.left,
                        itemRect.top,
                        itemRect.right,
                        itemRect.bottom);
            }
            mNormalDrawable.draw(canvas);
            if (mCurrentPasswordLength > index) {
                if (mInputHide) {
                    canvas.drawCircle(itemRect.centerX(),
                            itemRect.centerY(),
                            ((mTextPaint.descent() - mTextPaint.ascent()) / 2),
                            mRadiusPaint);
                } else {
                    Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                    float top = fontMetrics.top;
                    float bottom = fontMetrics.bottom;
                    canvas.drawText(mText.subSequence(index, index + 1) + "",
                            itemRect.centerX(),
                            (int) (itemRect.centerY() - top / 2 - bottom / 2),
                            mTextPaint);
                }
            }
            startLeft = itemRect.right + mItemMargin;
        }
        if (isDrawCursor && mCurrentPasswordLength < mMaxInputLength) {
//            float left =
//
//                    (itemWidth + mItemMargin) * mCurrentPasswordLength + itemWidth +
//                    itemWidth / 2 - mCursorWidth * 1.0f / 2;
            float left = itemWidth * mCurrentPasswordLength
                    + (mIncludeParentMargin ? mItemMargin * mCurrentPasswordLength : mItemMargin * (mCurrentPasswordLength - 1))
                    + itemWidth / 2 - mCursorWidth / 2.0f;
            float top = itemHeight / 2.0f - (mTextPaint.descent() - mTextPaint.ascent()) / 2.0f;
            canvas.drawRect(left, top,
                    left + mCursorWidth * 1.0f,
                    top + mTextPaint.descent() - mTextPaint.ascent(),
                    mCursorPaint);
        }
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return new ArrowKeyMovementMethod() {
            @Override
            protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode, int movementMetaState, KeyEvent event) {
                return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
            }

            @Override
            protected boolean left(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean right(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean up(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean down(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean pageUp(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean pageDown(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean top(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean bottom(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean lineStart(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean lineEnd(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean home(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            protected boolean end(TextView widget, Spannable buffer) {
                return false;
            }

            @Override
            public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
                return false;
            }

            @Override
            public boolean canSelectArbitrarily() {
                return false;
            }

            @Override
            public void initialize(TextView widget, Spannable text) {
                setSelection(text.length());
            }

            @Override
            public void onTakeFocus(TextView view, Spannable text, int dir) {

            }
        };
    }
}
