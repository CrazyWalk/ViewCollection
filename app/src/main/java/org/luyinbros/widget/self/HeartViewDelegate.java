package org.luyinbros.widget.self;

import android.animation.AnimatorSet;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;

import org.luyinbros.animation.AnimationFactory;

public class HeartViewDelegate {
    private AppCompatImageView mMainImageView;
    private AppCompatImageView mRippleImageView;
    private AnimatorSet mAnimatorSet;


    public HeartViewDelegate(ViewGroup parent) {
        mRippleImageView = new AppCompatImageView(parent.getContext());
        mMainImageView = new AppCompatImageView(parent.getContext());
        mAnimatorSet = AnimationFactory.heartbeatAnimator(mMainImageView, mRippleImageView);
        parent.addView(mRippleImageView);
        parent.addView(mMainImageView);
        mRippleImageView.setVisibility(View.GONE);
    }


    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(mMainImageView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mRippleImageView, widthMeasureSpec, heightMeasureSpec);
    }

    public void layout(int l, int t, int r, int b) {
        mMainImageView.layout(l,
                t,
                r,
                b);
        mRippleImageView.layout(l,
                t,
                r,
                b);
    }

    public int getMeasuredWidth() {
        return mMainImageView.getMeasuredWidth();
    }

    public int getMeasuredHeight() {
        return mMainImageView.getMeasuredHeight();
    }

    public void setRippleImageResource(int res) {
        mRippleImageView.setImageResource(res);
    }

    public void setImageResource(int res) {
        setImageResource(res, false);
    }

    public void setImageResource(int res, boolean isAnim) {
        mAnimatorSet.cancel();
        mMainImageView.setImageResource(res);
        if (isAnim) {
            mAnimatorSet.start();
        }
    }

    private void measureChild(View child, int parentWidthMeasureSpec,
                              int parentHeightMeasureSpec) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,
                0, lp.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
                0, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
}
