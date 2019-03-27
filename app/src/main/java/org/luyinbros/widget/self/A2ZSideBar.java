package org.luyinbros.widget.self;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class A2ZSideBar extends View {
    public List<String> mLetters;
    private OnLetterSelectedListener mOnLetterSelectedListener;
    private Paint mTextPaint = new TextPaint();
    private int mItemMargin = 10;
    private int mSelectedIndex = 0;

    public A2ZSideBar(Context context) {
        this(context, null);
    }

    public A2ZSideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public A2ZSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint.setTextSize(40);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setup(List<String> letters) {
        this.mLetters = letters;
        invalidate();
    }

    public void setOnLetterSelectedListener(OnLetterSelectedListener onLetterSelectedListener) {
        this.mOnLetterSelectedListener = onLetterSelectedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mLetters == null || mLetters.size() == 0) {
            setMeasuredDimension(0, 0);
        } else {
            float maxItemWidth = 0;
            int totalHeight = getPaddingTop() + getPaddingBottom();
            for (String s : mLetters) {
                maxItemWidth = Math.max(maxItemWidth, getTextWidth(s));
                totalHeight += getTextHeight();
            }
            totalHeight += (mLetters.size() - 1) * mItemMargin;
            setMeasuredDimension((int) (maxItemWidth + getPaddingLeft() + getPaddingRight()),
                    totalHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLetters != null && mLetters.size() != 0) {
            int baseLine = getPaddingTop();

            for (int index = 0; index < mLetters.size(); index++) {
                baseLine += getTextHeight() / 2;
                mTextPaint.setColor(index != mSelectedIndex ? 0xFFFF5921 : 0xFF000000);
                float pointX = getMeasuredWidth() / 2.0f;
                canvas.drawText(mLetters.get(index), pointX, baseLine, mTextPaint);
                if (index < mLetters.size() - 1) {
                    baseLine += mItemMargin + getTextHeight() / 2;
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        int newSelectedIndex = findLetterIndex(y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (newSelectedIndex != mSelectedIndex && mOnLetterSelectedListener != null && newSelectedIndex != -1) {
                    mOnLetterSelectedListener.onLetterSelected(newSelectedIndex);
                }
                mSelectedIndex = newSelectedIndex;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (newSelectedIndex != mSelectedIndex && mOnLetterSelectedListener != null && newSelectedIndex != -1) {
                    mOnLetterSelectedListener.onLetterSelected(newSelectedIndex);
                }
                mSelectedIndex = newSelectedIndex;
                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                return true;
        }
        return super.dispatchTouchEvent(event);
    }

    private float getTextWidth(String text) {
        return mTextPaint.measureText(text);
    }

    private int getTextHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return (int) (fontMetrics.bottom - fontMetrics.top);
    }

    private float getTextBaseLine(float center) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top;
        return center + height / 2 - fontMetrics.bottom;
    }

    private int findLetterIndex(float y) {
        if (mLetters == null || mLetters.size() == 0) {
            return -1;
        }
        if (y <= getPaddingTop()) {
            return -1;
        } else {
            int totalY = getPaddingTop();
            for (int index = 0; index < mLetters.size(); index++) {
                totalY += getTextHeight();
                if (y < totalY) {
                    return index;
                }
                totalY += mItemMargin;
            }
            return -1;
        }
    }

    public interface OnLetterSelectedListener {
        void onLetterSelected(int position);
    }

}
