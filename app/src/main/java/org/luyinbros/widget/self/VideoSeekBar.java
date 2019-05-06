package org.luyinbros.widget.self;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

public class VideoSeekBar extends View {
    private Paint mTextPaint = new Paint();
    private Paint mProgressPaint = new Paint();
    private Paint mSeekPaint = new Paint();
    private Paint mRemainProgressPaint = new Paint();
    private RectF mSeekRect = new RectF();
    private RectF mProgressRect = new RectF();
    private RectF mRemainRect = new RectF();
    private CharSequence mText;
    private final float mProgressHeight;
    private float mProgress;
    private boolean isSeek = false;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public VideoSeekBar(Context context) {
        this(context, null);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mProgressPaint.setAntiAlias(true);
        mSeekPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        mRemainProgressPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        setTextColor(0xFF000000);
        setTextSize(12);
        setProgressColor(0xFFEECCA2);
        mProgressHeight = px(2);
        mRemainProgressPaint.setColor(Color.BLACK);
    }

    public void setProgress(int progress) {
        if (!isSeek) {
            this.mProgress = progress;
            invalidate();
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener.onProgressChanged(this, progress, false);
            }
        }
    }

    public float getProgress() {
        return mProgress;
    }

    private void setSeekProgress(int progress) {
        this.mProgress = progress;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, progress, true);
        }
        invalidate();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    public void setText(CharSequence text) {
        mText = text;
        invalidate();
    }

    public void setTextSize(int spSize) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize, r.getDisplayMetrics()));
        invalidate();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        mSeekPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final float height = Math.max(getTextHeight(), mProgressHeight);
        setMeasuredDimension(width, (int) height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        mSeekRect.left = Math.min((width * mProgress / 100), (width - getTextWidth()));
        mSeekRect.right = mSeekRect.left + getTextWidth();
        mSeekRect.top = 0;
        mSeekRect.bottom = height;
        mProgressRect.left = 0;
        mProgressRect.right = width * mProgress / 100;
        mProgressRect.top = (height - mProgressHeight) / 2;
        mProgressRect.bottom = (height + mProgressHeight) / 2;
        mRemainRect.left = mProgressRect.right;
        mRemainRect.right = width;
        mRemainRect.top = mProgressRect.top;
        mRemainRect.bottom = mProgressRect.bottom;


        canvas.drawRect(mProgressRect, mProgressPaint);
        canvas.drawRect(mRemainRect, mRemainProgressPaint);
        canvas.drawRect(mSeekRect, mSeekPaint);
        if (mText != null) {
            final float pointX = mSeekRect.centerX();
            final float baseLine = getTextBaseLine(mSeekRect.height() / 2);
            canvas.drawText(mText.toString(), pointX, baseLine, mTextPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("VideoSeekBar", event.getX() + "");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (!isSeek && mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStartTrackingTouch(this);
                }
                isSeek = true;
                setSeekProgress((int) (event.getX() / getMeasuredWidth() * 100));
                return true;
            case MotionEvent.ACTION_UP:
                isSeek = true;
                setSeekProgress((int) (event.getX() / getMeasuredWidth() * 100));
                isSeek = false;
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                }
                return true;

        }
        return super.onTouchEvent(event);
    }

    private float getTextBaseLine(float center) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top;
        return center + height / 2 - fontMetrics.bottom;
    }

    private float px(int dpSize) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, r.getDisplayMetrics());
    }

    private float getTextWidth() {
        if (mText == null) {
            return 0;
        }
        return mTextPaint.measureText(mText, 0, mText.length());
    }

    private float getTextHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return (fontMetrics.bottom - fontMetrics.top);
    }

    public interface OnSeekBarChangeListener {

        void onProgressChanged(VideoSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(VideoSeekBar seekBar);

        void onStopTrackingTouch(VideoSeekBar seekBar);
    }
}
