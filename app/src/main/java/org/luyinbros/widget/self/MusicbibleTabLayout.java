package org.luyinbros.widget.self;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import org.luyinbros.utils.Dimens;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MusicbibleTabLayout extends ViewGroup {
    private ViewPager mViewPager;
    private List<TabTextView> tabViewList = new ArrayList<>();
    private int mSelectedTabTextColor = 0x74ffffff;
    private int mNormalTabTextColor = 0xFFFFFFFF;
    private int mSelectedTabBackgroundColor = 0x19FFFFFF;
    private int mNormalTabBackgroundColor = 0x00000000;
    private Paint mTipPaint = new Paint();
    private Paint mTipTextPaint = new Paint();
    private List<CharSequence> mTitleArray;
    private List<CharSequence> mTipTextArray;
    private int mSelectedIndex = -1;
    private RectF mTempTipRect = new RectF();

    public MusicbibleTabLayout(Context context) {
        this(context, null);
    }

    public MusicbibleTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicbibleTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTipPaint.setColor(Color.RED);
        mTipPaint.setAntiAlias(true);
        mTipTextPaint.setColor(Color.WHITE);
        mTipTextPaint.setTextSize(Dimens.px(getContext(), 11));
        mTipTextPaint.setStyle(Paint.Style.FILL);
        mTipTextPaint.setTextAlign(Paint.Align.CENTER);
        final List<CharSequence> titleArray = new ArrayList<>(3);
        titleArray.add("通知");
        titleArray.add("评论");
        titleArray.add("收藏");
        setTitleArray(titleArray);
        setTipText(0, "99+");
        setTipText(2, "2");

        notifySetDataChanged();
    }

    public void setupViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(mOnPagerChangeListener);
        }
        mViewPager = viewPager;
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(mOnPagerChangeListener);
        }
        notifySetDataChanged();
    }


    public void notifySetDataChanged() {
        tabViewList.clear();
        removeAllViews();
        final int itemCount = getItemCount();
        if (itemCount != 0) {
            for (int i = 0; i < itemCount; i++) {
                final int currentIndex = i;
                TabTextView tabTextView = createTab();
                tabViewList.add(tabTextView);
                tabTextView.setText(mTitleArray.get(i));
                tabTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(currentIndex, true);
                        }
                    }
                });
                addView(tabTextView);
            }
        }
        if (itemCount == 0) {
            setSelectedTab(-1);
        } else {
            setSelectedTab(0);
        }
        requestLayout();
    }

    public void setTipText(int position, String text) {
        mTipTextArray.set(position, text);
        invalidate();
    }

    public void setTitleArray(List<CharSequence> titleArray) {
        if (titleArray == null || titleArray.size() == 0) {
            if (mTitleArray != null) {
                mTitleArray.clear();
            }
            if (mTipTextArray != null) {
                mTipTextArray.clear();
            }
            requestLayout();
        } else {
            final int titleSize = titleArray.size();
            if (mTitleArray == null) {
                mTitleArray = new ArrayList<>(titleSize);
            } else {
                mTitleArray.clear();
            }
            if (mTipTextArray == null) {
                mTipTextArray = new ArrayList<>(titleSize);
            } else {
                mTipTextArray.clear();
            }
            mTitleArray.addAll(titleArray);
            for (int i = 0; i < titleSize; i++) {
                mTipTextArray.add(null);
            }
            requestLayout();
        }
    }

    private int getItemCount() {
        return mTitleArray == null ? 0 : mTipTextArray.size();
    }

    private TabTextView createTab() {
        TabTextView tabTextView = new TabTextView(getContext());
        {
            final int[][] states = new int[2][];
            final int[] colors = new int[2];
            states[0] = new int[]{android.R.attr.state_selected};
            colors[0] = mSelectedTabTextColor;

            states[1] = new int[]{android.R.attr.state_enabled};
            colors[1] = mNormalTabTextColor;
            tabTextView.setTextColor(new ColorStateList(states, colors));
        }
        {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(mSelectedTabBackgroundColor));
            stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable(mNormalTabBackgroundColor));
            tabTextView.setBackground(stateListDrawable);
        }
        tabTextView.setPadding(Dimens.px(getContext(), 10),
                Dimens.px(getContext(), 4),
                Dimens.px(getContext(), 10),
                Dimens.px(getContext(), 4));
        tabTextView.setTextSize(13);
        tabTextView.setGravity(Gravity.CENTER);
        return tabTextView;

    }

    private void setSelectedTab(int position) {
        if (mSelectedIndex != -1) {
            getChildAt(mSelectedIndex).setSelected(false);
        }
        mSelectedIndex = position;
        if (mSelectedIndex != -1) {
            getChildAt(mSelectedIndex).setSelected(true);
        }
    }

    private void setSelectedTabByAnim(int position) {
        View previousView = null;
        View nextView = null;
        if (mSelectedIndex != -1) {
            previousView = getChildAt(mSelectedIndex);
        }
        mSelectedIndex = position;
        if (mSelectedIndex != -1) {
            nextView = getChildAt(mSelectedIndex);
        }
        ValueAnimator previousAnimator = null;
        if (previousView != null) {
            previousAnimator = ValueAnimator.ofArgb(mSelectedTabBackgroundColor, mNormalTabBackgroundColor)
            ;
        }
        ValueAnimator nextAnimator = null;
        if (nextView != null) {
            nextAnimator = ValueAnimator.ofArgb(mNormalTabBackgroundColor, mSelectedTabBackgroundColor);
        }

        AnimatorSet animationSet = new AnimatorSet()
                .setDuration(250);
        animationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        animationSet.setInterpolator(new LinearOutSlowInInterpolator());
        AnimatorSet.Builder builder = null;
        if (previousAnimator != null) {
            builder = animationSet.play(previousAnimator);
        }
        if (nextAnimator != null) {
            if (builder == null) {
                builder = animationSet.play(nextAnimator);
            } else {
                builder.after(nextAnimator);
            }
        }
        animationSet.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        if (childCount <= 1) {
            setMeasuredDimension(0, 0);
        } else {
            View child;
            int maxHeight = 0;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                child.measure(getChildMeasureSpec(widthMeasureSpec, 0, child.getLayoutParams().width),
                        getChildMeasureSpec(widthMeasureSpec, 0, child.getLayoutParams().height));
                maxHeight = Math.max(child.getMeasuredHeight(), maxHeight);
            }
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxHeight + getPaddingTop() + getPaddingLeft());
        }


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        if (childCount == 2) {
            final int midX = getMeasuredWidth() / 2;
            final int midY = getMeasuredHeight() / 2;
            final int distanceMid = Dimens.px(getContext(), 21.5f);
            View child;
            child = getChildAt(0);
            child.layout(
                    midX - distanceMid - child.getMeasuredWidth(),
                    midY - child.getMeasuredHeight() / 2,
                    midX - distanceMid,
                    midY + child.getMeasuredHeight() / 2
            );
            child = getChildAt(1);
            child.layout(
                    midX + distanceMid,
                    midY - child.getMeasuredHeight() / 2,
                    midX + distanceMid + child.getMeasuredWidth(),
                    midY + child.getMeasuredHeight() / 2
            );
        } else if (childCount == 3) {
            final int minPosition = childCount / 2;
            final int startPosition = 0;
            final int endPosition = childCount - 1;
            View child;
            final int midX = getMeasuredWidth() / 2;
            final int midY = getMeasuredHeight() / 2;
            child = getChildAt(minPosition);
            child.layout(
                    midX - child.getMeasuredWidth() / 2,
                    midY - child.getMeasuredHeight() / 2,
                    midX + child.getMeasuredWidth() / 2,
                    midY + child.getMeasuredHeight() / 2
            );

            child = getChildAt(startPosition);
            child.layout(
                    getPaddingLeft(),
                    midY - child.getMeasuredHeight() / 2,
                    getPaddingLeft() + child.getMeasuredWidth(),
                    midY + child.getMeasuredHeight() / 2
            );

            child = getChildAt(endPosition);
            child.layout(
                    getMeasuredWidth() - child.getMeasuredWidth() - getPaddingEnd(),
                    midY - child.getMeasuredHeight() / 2,
                    getMeasuredWidth() - getPaddingEnd(),
                    midY + child.getMeasuredHeight() / 2
            );

        } else if (childCount == 4) {
            final int midX = getMeasuredWidth() / 2;
            final int midY = getMeasuredHeight() / 2;
            final int distanceMid = Dimens.px(getContext(), 29f);
            View child;
            child = getChildAt(0);
            child.layout(
                    getPaddingLeft(),
                    midY - child.getMeasuredHeight() / 2,
                    getPaddingLeft() + child.getMeasuredWidth(),
                    midY + child.getMeasuredHeight() / 2
            );
            child = getChildAt(1);
            child.layout(
                    midX - distanceMid - child.getMeasuredWidth(),
                    midY - child.getMeasuredHeight() / 2,
                    midX - distanceMid,
                    midY + child.getMeasuredHeight() / 2
            );
            child = getChildAt(2);
            child.layout(
                    midX + distanceMid,
                    midY - child.getMeasuredHeight() / 2,
                    midX + distanceMid + child.getMeasuredWidth(),
                    midY + child.getMeasuredHeight() / 2
            );

            child = getChildAt(3);
            child.layout(
                    getMeasuredWidth() - child.getMeasuredWidth() - getPaddingEnd(),
                    midY - child.getMeasuredHeight() / 2,
                    getMeasuredWidth() - getPaddingEnd(),
                    midY + child.getMeasuredHeight() / 2
            );
        } else if (childCount == 5) {
            final int minPosition = childCount / 2;
            final int startPosition = 0;
            final int endPosition = childCount - 1;
            View child;
            final int midX = getMeasuredWidth() / 2;
            final int midY = getMeasuredHeight() / 2;
            child = getChildAt(minPosition);
            child.layout(
                    midX - child.getMeasuredWidth() / 2,
                    midY - child.getMeasuredHeight() / 2,
                    midX + child.getMeasuredWidth() / 2,
                    midY + child.getMeasuredHeight() / 2
            );

            child = getChildAt(startPosition);
            child.layout(
                    getPaddingLeft(),
                    midY - child.getMeasuredHeight() / 2,
                    getPaddingLeft() + child.getMeasuredWidth(),
                    midY + child.getMeasuredHeight() / 2
            );

            child = getChildAt(endPosition);
            child.layout(
                    getMeasuredWidth() - child.getMeasuredWidth() - getPaddingEnd(),
                    midY - child.getMeasuredHeight() / 2,
                    getMeasuredWidth() - getPaddingEnd(),
                    midY + child.getMeasuredHeight() / 2
            );

            {
                int innerMidX = midX / 2 + getChildAt(startPosition).getRight() / 2;
                child = getChildAt(1);
                child.layout(
                        innerMidX - child.getMeasuredWidth() / 2,
                        midY - child.getMeasuredHeight() / 2,
                        innerMidX + child.getMeasuredWidth() / 2,
                        midY + child.getMeasuredHeight() / 2
                );
            }
            {
                {
                    int innerMidX = getChildAt(endPosition).getLeft() / 2 + midX / 2;
                    child = getChildAt(3);
                    child.layout(
                            innerMidX - child.getMeasuredWidth() / 2,
                            midY - child.getMeasuredHeight() / 2,
                            innerMidX + child.getMeasuredWidth() / 2,
                            midY + child.getMeasuredHeight() / 2
                    );
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int childCount = getChildCount();
        if (childCount > 2) {
            View child;
            int top;
            float right;
            float cx;
            float cy;
            final float radius = Dimens.px(getContext(), 6f);
            CharSequence tipText;
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                tipText = mTipTextArray.get(i);
                top = child.getTop() + Dimens.px(getContext(), 4f);
                right = child.getRight() - Dimens.px(getContext(), 10f);
                if (tipText != null && tipText.length() != 0) {
                    if (tipText.length() == 1) {
                        cx = right + radius / 2;
                        cy = top;
                        canvas.drawCircle(cx, cy, radius, mTipPaint);
                        drawTipText(tipText, canvas, cx, cy);
                    } else {
                        mTempTipRect.left = right - Dimens.px(getContext(), 4f);
                        mTempTipRect.top = top - Dimens.px(getContext(), 6f);
                        mTempTipRect.right = mTempTipRect.left + getTextWidth(tipText) + Dimens.px(getContext(), 4f);
                        mTempTipRect.bottom = mTempTipRect.top + getTextHeight() + Dimens.px(getContext(), 2f);
                        canvas.drawRoundRect(mTempTipRect,mTempTipRect.centerX(),mTempTipRect.centerY(), mTipPaint);
                        drawTipText(tipText, canvas, mTempTipRect.centerX(), mTempTipRect.centerY());
                    }
                }


            }
        }
    }

    private float getTextWidth(CharSequence text) {
        if (text == null) {
            return 0;
        }
        return mTipTextPaint.measureText(text, 0, text.length());
    }

    public void drawTipText(CharSequence text,
                            Canvas canvas,
                            float centerX,
                            float centerY) {
        if (text != null) {
            final float baseLine = getTextBaseLine(centerY);
            canvas.drawText(text.toString(), centerX, baseLine, mTipTextPaint);
        }
    }

    private float getTextBaseLine(float center) {
        Paint.FontMetrics fontMetrics = mTipTextPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top;
        return center + height / 2 - fontMetrics.bottom;
    }

    private float getTextHeight() {
        Paint.FontMetrics fontMetrics = mTipTextPaint.getFontMetrics();
        return (fontMetrics.bottom - fontMetrics.top);
    }

    private ViewPager.OnPageChangeListener mOnPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setSelectedTab(position);

        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    };

    private static class TabTextView extends AppCompatTextView {

        public TabTextView(Context context) {
            super(context);
        }


    }
}
