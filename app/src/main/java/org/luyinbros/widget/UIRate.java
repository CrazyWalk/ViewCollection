package org.luyinbros.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class UIRate extends View {
    private int mDistance;
    private int mRateCount;
    private float mRate;
    private float mStep;
    private boolean mEditEnable;
    private Drawable mFillDrawable;
    private Drawable mEmptyDrawable;
    private int mSrcWidth;
    private int mSrcHeight;
    private Paint mPaint;
    private Bitmap mFillBitmap;
    private OnRateChangeListener mOnRateChangeListener;

    public UIRate(Context context) {
        this(context, null);
    }

    public UIRate(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIRate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UIRate);
        setDistance(a.getDimensionPixelOffset(R.styleable.UIRate_rateView_distance, 20));
        setRateCount(a.getInt(R.styleable.UIRate_rateView_rateCount, 5));
        setRate(a.getFloat(R.styleable.UIRate_rateView_rate, 0f));
        setStep(a.getFloat(R.styleable.UIRate_rateView_step, 1f));
        setFillDrawable(a.getDrawable(R.styleable.UIRate_rateView_fullSrc));
        setEmptyDrawable(a.getDrawable(R.styleable.UIRate_rateView_emptySrc));
        setItemSize(a.getDimensionPixelOffset(R.styleable.UIRate_rateView_srcWidth, 0),
                a.getDimensionPixelOffset(R.styleable.UIRate_rateView_srcHeight, 0));
        setEditEnable(a.getBoolean(R.styleable.UIRate_rateView_editEnable, true));
        a.recycle();

    }

    /**
     * 是否可编辑
     *
     * @param editEnable true 可编辑 false 不可编辑
     */
    public void setEditEnable(boolean editEnable) {
        this.mEditEnable = editEnable;
    }

    /**
     * 设置评分控件之间的距离
     *
     * @param distance 当前设置的距离
     */
    public void setDistance(@Px int distance) {
        this.mDistance = distance;
        invalidate();
    }

    /**
     * 评分控件监听事件
     *
     * @param onRateChangeListener 编辑时的评分监听事件
     */
    public void setOnRateChangeListener(OnRateChangeListener onRateChangeListener) {
        this.mOnRateChangeListener = onRateChangeListener;
    }

    /**
     * 设置评分图片数量
     *
     * @param rateCount 评分图片数量
     */
    public void setRateCount(int rateCount) {
        this.mRateCount = rateCount;
        invalidate();
    }

    /**
     * 设置评分
     *
     * @param rate 评分数
     */
    public void setRate(float rate) {
        this.mRate = rate;
        invalidate();
    }

    /**
     * 设置步长
     *
     * @param step (0,1]
     */
    public void setStep(float step) {
        this.mStep = step;
        if (step > 1f) {
            step = 1f;
        }
        invalidate();
    }

    /**
     * 获取当前评分
     *
     * @return 评分
     */
    public float getRate() {
        return mRate;
    }

    /**
     * 设置 填充的评分图片
     *
     * @param fillDrawable 填充图片
     */
    public void setFillDrawable(Drawable fillDrawable) {
        this.mFillDrawable = fillDrawable;
        if (mFillBitmap != null) {
            mFillBitmap.recycle();
            mFillBitmap = null;
        }

        if (mFillDrawable != null) {
            mFillBitmap = drawableToBitmap(mFillDrawable);
            if (mFillBitmap != null) {
                mPaint.setShader(new BitmapShader(mFillBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            }
        }

        invalidate();
    }

    /**
     * 设置空白的评分图片
     *
     * @param emptyDrawable 填充图片
     */
    public void setEmptyDrawable(Drawable emptyDrawable) {
        this.mEmptyDrawable = emptyDrawable;
        invalidate();
    }

    /**
     * 设置评分图片的宽高
     *
     * @param width  宽
     * @param height 高
     */
    public void setItemSize(int width, int height) {
        this.mSrcHeight = height;
        this.mSrcWidth = width;
        if (mFillDrawable != null) {
            if (mFillBitmap != null) {
                mFillBitmap.recycle();
            }
            mFillBitmap = drawableToBitmap(mFillDrawable);
            if (mFillBitmap != null) {
                mPaint.setShader(new BitmapShader(mFillBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            }
        }

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (shouldDraw()) {
            setMeasuredDimension(mSrcWidth * mRateCount
                            + mDistance * (mRateCount - 1)
                            + getPaddingLeft() + getPaddingRight()
                    , mSrcHeight + getPaddingTop() + getPaddingBottom());
        } else {
            setMeasuredDimension(getPaddingLeft() + getPaddingRight(),
                    getPaddingTop() + getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldDraw()) {
            final int top = getPaddingTop();
            final int bottom = getPaddingTop() + mSrcHeight;
            for (int i = 0; i < mRateCount; i++) {
                mEmptyDrawable.setBounds(getPaddingLeft() + (mDistance + mSrcWidth) * i,
                        top,
                        getPaddingLeft() + (mDistance + mSrcWidth) * i + mSrcWidth,
                        bottom);
                mEmptyDrawable.draw(canvas);
            }
            if (mRate > 1) {
                canvas.translate(getPaddingLeft(), top);
                if (mRate - rateInt() == 0) {
                    for (int i = 0; i < rateInt(); i++) {
                        canvas.drawRect(0, 0, mSrcWidth, mSrcHeight, mPaint);
                        canvas.translate(mDistance + mSrcWidth, 0);
                    }
                } else {
                    for (int i = 0; i < rateInt(); i++) {
                        canvas.drawRect(0, 0, mSrcWidth, mSrcHeight, mPaint);
                        canvas.translate(mDistance + mSrcWidth, 0);
                    }
                    canvas.drawRect(0, 0, mSrcWidth * rateDecimals(), mSrcHeight, mPaint);
                }

            } else {
                canvas.translate(getPaddingLeft(), top);
                canvas.drawRect(0, 0, mSrcWidth * mRate, mSrcHeight, mPaint);
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEditEnable && shouldDraw()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    touchRating(event.getX());
                    break;
                case MotionEvent.ACTION_DOWN:
                    touchRating(event.getX());
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    void touchRating(float x) {
        if (x < 0f) x = 0;
        if (x > getMeasuredWidth()) x = getMeasuredWidth();

        if (x <= getPaddingLeft()) {
            setRate(0f);
            if (mOnRateChangeListener != null) {
                mOnRateChangeListener.onTouchChange(0f);
            }
        } else {
            if (mRateCount == 1) {
                float leftLimit = getPaddingLeft();
                float viewLimit = leftLimit + mSrcWidth;
                float rightLimit = viewLimit + getPaddingRight();
                if (x >= viewLimit && x <= rightLimit) {
                    setRate(1f);
                } else {
                    float rateDecimals = (x - leftLimit) / (viewLimit - leftLimit);
                    setRate(calcRateDecimals(rateDecimals));
                }

            } else {
                float leftLimit;
                float viewLimit;
                float rightLimit = 0f;
                int currentStarPos;
                for (int i = 0; i < mRateCount; i++) {
                    currentStarPos = i;
                    if (currentStarPos == 0) {
                        leftLimit = getPaddingLeft();
                        viewLimit = leftLimit + mSrcWidth;
                        rightLimit = viewLimit + mDistance;
                    } else if (i == mRateCount - 1) {
                        leftLimit = rightLimit;
                        viewLimit = leftLimit + mSrcWidth;
                        rightLimit = viewLimit + getPaddingRight();
                    } else {
                        leftLimit = rightLimit;
                        viewLimit = leftLimit + mSrcWidth;
                        rightLimit = viewLimit + mDistance;
                    }
                    if (x <= rightLimit) {
                        if (x >= viewLimit) {
                            setRate(currentStarPos + 1);
                            if (mOnRateChangeListener != null) {
                                mOnRateChangeListener.onTouchChange(currentStarPos + 1);
                            }
                        } else {
                            float rateDecimals = (x - leftLimit) / (viewLimit - leftLimit);
                            setRate(currentStarPos + calcRateDecimals(rateDecimals));
                            if (mOnRateChangeListener != null) {
                                mOnRateChangeListener.onTouchChange(currentStarPos + calcRateDecimals(rateDecimals));
                            }
                        }
                        break;
                    }
                }

            }

        }
    }

    private float calcRateDecimals(float rateDecimals) {
        if ((int) mStep == 1) {
            return rateDecimals > 0.25f ? 1f : 0f;
        } else {
            return rateDecimals > 0.25f ? 1f : 0f;
        }
    }

    boolean shouldDraw() {
        return mRateCount != 0 && mSrcWidth != 0 && mSrcHeight != 0 && mFillBitmap != null && mEmptyDrawable != null;
    }

    private float rateDecimals() {
        return Math.round((mRate - (int) (mRate)) * 10) * 1.0f / 10;
    }

    private int rateInt() {
        return (int) mRate;
    }


    @Nullable
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        if (mSrcWidth <= 0 || mSrcHeight <= 0) {
            mSrcWidth = drawable.getIntrinsicWidth();
            mSrcHeight = drawable.getIntrinsicHeight();
            if (mSrcWidth <= 0 || mSrcHeight <= 0) {
                return null;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(mSrcWidth, mSrcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, mSrcWidth, mSrcHeight);
        drawable.draw(canvas);
        return bitmap;
    }

    public interface OnRateChangeListener {
        void onTouchChange(float rating);
    }
}
