package org.luyinbros.widget.self;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Description:
 * Author：洪培林
 * Created Time:2016/8/25 13:22
 */
public class DefaultBannerIndicator extends View implements BannerView.Indicator {
    public static final String TAG = "CircleIndicator";
    private PagerView mPagerView;
    private int mCurrentPage = 0;
    private int mRadius = 6;
    private final Paint mPaintPageFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(ANTI_ALIAS_FLAG);
    private final Paint mPaintFill = new Paint(ANTI_ALIAS_FLAG);
    private int interval;

    public DefaultBannerIndicator(Context context) {
        this(context, null);
    }

    public DefaultBannerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultBannerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaintPageFill.setStyle(Paint.Style.FILL);
        mPaintPageFill.setColor(0xFFFFD321);

        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(0xFFFFD321);

        mPaintStroke.setStyle(Paint.Style.FILL);
        mPaintStroke.setColor(0xFF999999);
        mPaintStroke.setStrokeWidth(1);
    }

    public void setSelectedColor(int color) {
        mPaintPageFill.setColor(color);
        mPaintFill.setColor(color);
        invalidate();
    }

    public void setUnselectedColor(int color) {
        mPaintStroke.setColor(color);
        invalidate();
    }

    public void setInterval(int interval) {
        this.interval = interval;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPagerView == null) {
            return;
        }
        final int count = getPageSize();

        if (count == 0) {
            return;
        }
        int cx = getPaddingLeft() + mRadius;
        int cy = getPaddingTop() + mRadius;
        for (int i = 0; i < count; i++) {
            if (i == mCurrentPage) {
                canvas.drawCircle(cx, cy, mRadius, mPaintPageFill);
            } else {
                canvas.drawCircle(cx, cy, mRadius, mPaintStroke);
            }
            cx += 2 * mRadius + interval;
        }
//        int longSize;
//        int longPaddingBefore;
//        int longPaddingAfter;
//        int shortPaddingBefore;
//
//        longSize = getWidth();
//        longPaddingBefore = getPaddingLeft();
//        longPaddingAfter = getPaddingRight();
//        shortPaddingBefore = getPaddingTop();
//
//
//        final float threeRadius = 3 * mRadius;
//        final float shortOffset = shortPaddingBefore + mRadius;
//
//        float longOffset = longPaddingBefore + mRadius;
//
//        float dX;
//        float dY;
//
//        //draw stroke circle
//        for (int iLoop = 0; iLoop < count; iLoop++) {
//            dY = shortOffset;
//            dX = longOffset + iLoop * threeRadius;
//
//            canvas.drawCircle(dX, dY, mRadius, mPaintStroke);
//        }
//        //draw page circle
//        float cx = mCurrentPage * threeRadius;
//
//        dX = cx + longOffset;
//        dY = shortOffset;
//        canvas.drawCircle(dX, dY, mRadius, mPaintPageFill);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureLongEdge(widthMeasureSpec), measureShortEdge(heightMeasureSpec));

    }

    /**
     * 计算长边
     */
    private int measureLongEdge(int measureSpec) {
        int measureResult;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            measureResult = specSize;
        } else {
            if (mPagerView == null) {
                measureResult = 0;
            } else {
                final int count = getPageSize();
                measureResult = getPaddingLeft() + getPaddingRight() + 2 * mRadius * count + (count - 1) * interval;
            }
            if (specMode == MeasureSpec.AT_MOST) {
                measureResult = Math.min(measureResult, specSize);
            }
        }
        return measureResult;
    }

    private int getPageSize() {
        return mPagerView == null ? 0 : mPagerView.getItemCount();
    }

    /**
     * 计算短边
     */
    private int measureShortEdge(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            //We were told how big to be
            result = specSize;
        } else {
            //Measure the height  topPadding + diameter + bottomPadding
            result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public void setPagerView(@Nullable PagerView pagerView) {
        this.mPagerView = pagerView;
    }

    @Override
    public void onSetDataChanged() {
        requestLayout();
        invalidate();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        invalidate();

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.currentPage;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = mCurrentPage;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPage;

         SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
