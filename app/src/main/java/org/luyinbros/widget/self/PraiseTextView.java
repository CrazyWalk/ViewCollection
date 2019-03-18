package org.luyinbros.widget.self;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.luyinbros.widget.R;

public class PraiseTextView extends ViewGroup {
    private ImageView praiseImageView;
    private TextView mTextView;
    private TextView mNumberTextView;
    private AnimatorSet mNumberAnimatorSet;

    public PraiseTextView(Context context) {
        this(context, null);
    }

    public PraiseTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        {
            praiseImageView = new AppCompatImageView(context);
            praiseImageView.setImageResource(R.mipmap.ic_praise_unselected);
        }

        {
            mTextView = new AppCompatTextView(context);
        }
        {
            mNumberTextView = new AppCompatTextView(context);
            mNumberTextView.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.OVAL);
            gradientDrawable.setColor(Color.RED);
            mNumberTextView.setBackground(gradientDrawable);
            mNumberTextView.setVisibility(GONE);
            mNumberTextView.setGravity(Gravity.CENTER);
            mNumberTextView.setTextColor(Color.WHITE);
            mNumberTextView.setText("+1");
        }
        addView(praiseImageView);
        addView(mTextView);
        addView(mNumberTextView);
        mNumberAnimatorSet = new AnimatorSet();
        mNumberAnimatorSet.setInterpolator(new LinearInterpolator());
        mNumberAnimatorSet.setDuration(600)
                .play(ObjectAnimator.ofFloat(mNumberTextView, "translationY", 0, -100))
                .with(ObjectAnimator.ofFloat(mNumberTextView, "alpha", 1.0f, 0f));
        mNumberAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mNumberTextView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(praiseImageView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mTextView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mNumberTextView, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(praiseImageView.getMeasuredWidth() + mTextView.getMeasuredWidth(),
                Math.max(praiseImageView.getMeasuredHeight(), mTextView.getMeasuredHeight()));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = (getMeasuredHeight() - praiseImageView.getMeasuredHeight()) / 2;
        praiseImageView.layout(left,
                top,
                left + praiseImageView.getMeasuredWidth(),
                top + praiseImageView.getMeasuredHeight());

        left += mTextView.getMeasuredWidth();
        {
            int iTop = (getMeasuredHeight() - mTextView.getMeasuredHeight()) / 2;
            mTextView.layout(left,
                    iTop,
                    left + mTextView.getMeasuredWidth(),
                    iTop + mTextView.getMeasuredHeight());
        }
        mNumberTextView.layout(
                0,
                -mNumberTextView.getMeasuredHeight(),
                mNumberTextView.getMeasuredWidth(),
                0
        );
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mNumberAnimatorSet.cancel();
        if (selected) {
            mNumberTextView.setVisibility(VISIBLE);
            mNumberAnimatorSet.start();
        }else{
            mNumberTextView.setVisibility(GONE);
        }
    }
}
