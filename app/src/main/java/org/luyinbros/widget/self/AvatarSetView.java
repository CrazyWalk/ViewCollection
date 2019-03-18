package org.luyinbros.widget.self;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.util.LinkedList;

public class AvatarSetView extends ViewGroup {
    private LinkedList<ImageView> linkedList;
    private final int overlapSize;
    private int mMaxVisibleCount = 3;
    private AnimatorSet mAnimatorSet;

    public AvatarSetView(Context context) {
        this(context, null);
    }

    public AvatarSetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarSetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linkedList = new LinkedList<>();
        overlapSize = 20;
        insertLastView();
        insertLastView();
        insertLastView();
        insertLastView();
    }

    public void startAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet = create();
        mAnimatorSet.start();
    }

    public void insertLastView() {
        int currentSize = linkedList.size();
        ImageView lastView = newRandomImageView();
        linkedList.addLast(lastView);
        addView(lastView);

//        if (currentSize >= mMaxVisibleCount) {
//            ImageView firstView = linkedList.getFirst();
//            linkedList.removeFirst();
//            removeView(firstView);
//        }
    }

    public void insertFirstView() {
        int currentSize = linkedList.size();
        ImageView firstView = newRandomImageView();
        linkedList.addFirst(firstView);
        addView(firstView, 0);

        if (currentSize >= mMaxVisibleCount) {
            ImageView lastView = linkedList.getLast();
            linkedList.removeLast();
            removeView(lastView);
        }
    }


    private ImageView newRandomImageView() {
        ImageView imageView = newImageView();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.argb(
                255,
                (int) (Math.random() * 266),
                (int) (Math.random() * 266),
                (int) (Math.random() * 266)));
        gradientDrawable.setShape(GradientDrawable.OVAL);
        imageView.setImageDrawable(gradientDrawable);
        return imageView;
    }


    private ImageView newImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(60, 60));
        return imageView;
    }

    private AnimatorSet create() {
        AnimatorSet animatorSet = new AnimatorSet();

        AnimatorSet hideAnimatorSet = new AnimatorSet();
        hideAnimatorSet.setDuration(100);
        hideAnimatorSet.setInterpolator(new LinearInterpolator());
        hideAnimatorSet.play(ObjectAnimator.ofFloat(linkedList.get(0), "alpha", 0f));


        AnimatorSet advanceAnimatorSet = new AnimatorSet();
        advanceAnimatorSet.setDuration(400);
        advanceAnimatorSet.setInterpolator(new OvershootInterpolator());
        advanceAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(linkedList.get(1), "translationX", 0, -60 + overlapSize),
                ObjectAnimator.ofFloat(linkedList.get(2), "translationX", 0, -60 + overlapSize),
                ObjectAnimator.ofFloat(linkedList.get(3), "alpha", 0, 1f)
        );

        animatorSet.playTogether(hideAnimatorSet, advanceAnimatorSet);
        return animatorSet;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = linkedList.size();
        if (size > 0) {
            int parentWidth = 0;
            for (ImageView imageView : linkedList) {
                if (shouldLayout(imageView)) {
                    measureChild(imageView, widthMeasureSpec, heightMeasureSpec);
                    parentWidth += imageView.getMeasuredWidth();
                }

            }
            parentWidth = parentWidth - (size - 1) * overlapSize;

            setMeasuredDimension(parentWidth, linkedList.getFirst().getMeasuredHeight());
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int size = linkedList.size();
        if (size > 0) {
            int left = 0;
            for (ImageView imageView : linkedList) {
                if (shouldLayout(imageView)) {
                    imageView.layout(left,
                            0,
                            left + imageView.getMeasuredWidth(),
                            imageView.getMeasuredHeight());
                    left += imageView.getMeasuredWidth() - overlapSize;
                }
            }
        }
    }

    private boolean shouldLayout(View v) {
        return v.getParent() == this && v.getVisibility() == VISIBLE;
    }


}
