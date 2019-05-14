package org.luyinbros.widget.self;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.luyinbros.widget.R;


public class BadgeImageView extends AppCompatImageView {
    private int[] mTempBitmapParam = new int[4];
    private float[] mTempMatrix = new float[9];
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint();

    private int mBadgeColor;
    private int mBadgeTextColor;
    private int mBadgeTextSize;
    private int mBadgeRadiusSize;
    private int mCenterTranslationX;
    private int mCenterTranslationY;
    private String text;
    private boolean showShape = false;

    public BadgeImageView(Context context) {
        this(context, null);
    }

    public BadgeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeImageView);
        setBadgeColor(a.getColor(R.styleable.BadgeImageView_imageBadge_color, 0xFFFF270E));
        setBadgeTextColor(a.getColor(R.styleable.BadgeImageView_imageBadge_textColor, 0xFF333333));
        setBadgeTextSize(a.getDimensionPixelOffset(R.styleable.BadgeImageView_imageBadge_textSize, 10));
        setBadgeRadiusSize(a.getDimensionPixelOffset(R.styleable.BadgeImageView_imageBadge_radiusSize, 5));
        setCenterTranslationX(a.getDimensionPixelOffset(R.styleable.BadgeImageView_imageBadge_translationX, 0));
        setCenterTranslationY(a.getDimensionPixelOffset(R.styleable.BadgeImageView_imageBadge_translationY, 0));
        setScaleType(ScaleType.CENTER);
        a.recycle();
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setShowShape(boolean showShape) {
        this.showShape = showShape;
        invalidate();
    }

    public void setCenterTranslationX(int centerTranslationX) {
        this.mCenterTranslationX = centerTranslationX;
    }


    public void setCenterTranslationY(int centerTranslationY) {
        this.mCenterTranslationY = centerTranslationY;
    }


    public void setBadgeColor(int color) {
        this.mBadgeColor = color;
        mPaint.setColor(mBadgeColor);
        invalidate();
    }

    public void setBadgeTextColor(int color) {
        this.mBadgeTextColor = color;
        mTextPaint.setColor(color);
        invalidate();
    }

    public void setBadgeTextSize(int size) {
        this.mBadgeTextSize = size;
        mTextPaint.setTextSize(size);
        invalidate();
    }

    public void setBadgeRadiusSize(int size) {
        this.mBadgeRadiusSize = size;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showShape) {
            calcBitmapParam();
            int cx = mTempBitmapParam[0] + mTempBitmapParam[2] + mCenterTranslationX;
            int cy = mTempBitmapParam[1] + mCenterTranslationY;
            canvas.drawCircle(cx,
                    cy,
                    mBadgeRadiusSize,
                    mPaint);
            drawText(canvas, cx, cy);
        }


    }

    private Rect mTextRect = new Rect();

    private void drawText(Canvas canvas, int cx, int cy) {
        if (!TextUtils.isEmpty(text)) {
            mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
            canvas.drawText(text, cx, cy - (mTextRect.top + mTextRect.bottom) / 2, mTextPaint);
        }
    }



    private void calcBitmapParam() {

        Matrix matrix = getImageMatrix();
        matrix.getValues(mTempMatrix);

        // x方向上的偏移量(单位px)
        mTempBitmapParam[0] = (int) mTempMatrix[Matrix.MTRANS_X];
        // y方向上的偏移量(单位px)
        mTempBitmapParam[1] = (int) mTempMatrix[Matrix.MTRANS_Y];

        mTempBitmapParam[0] += getPaddingLeft();
        mTempBitmapParam[1] += getPaddingTop();

        mTempBitmapParam[2] = (int) (mTempMatrix[Matrix.MSCALE_X] * getDrawable().getBounds().width());
        mTempBitmapParam[3] = (int) (mTempMatrix[Matrix.MSCALE_Y] * getDrawable().getBounds().height());

    }
}
