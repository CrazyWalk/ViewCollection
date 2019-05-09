package org.luyinbros.widget.self;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class AvatarSetView extends ViewGroup {
    private List<ImageView> linkedList;
    private final int overlapSize;
    private AnimatorSet mAnimatorSet;
    private Adapter mAdapter;

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

    }

    public void startAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet = create();
        mAnimatorSet.start();
    }


    public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.mAvatarSetView = null;
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.mAvatarSetView = this;
        }
        notifyDataSetChanged();
    }


    void notifyDataSetChanged() {
        linkedList.clear();
        removeAllViews();
        if (mAdapter != null) {
            final int totalCount = mAdapter.getCount();
            if (totalCount != 0) {
                final int maxVisibleCount = mAdapter.getMaxVisibleCount();
                if (maxVisibleCount != 0) {
                    final int visibleIndexStart = totalCount - maxVisibleCount < 0 ? 0 : totalCount - maxVisibleCount;
                    if (visibleIndexStart < totalCount) {
                        linkedList = new ArrayList<>(maxVisibleCount);
                        ImageView $imageView;
                        for (int i = visibleIndexStart; i < totalCount; i++) {
                            $imageView = newImageView();
                            linkedList.add($imageView);
                            mAdapter.onBindAvatarItemView($imageView, i);
                            addView($imageView);
                        }
                    }
                }

            }
        }

    }

    public void notifyLastItemInserted() {
        if (mAdapter != null) {
            final int count = mAdapter.getCount();
            final int maxVisibleCount = mAdapter.getMaxVisibleCount();
            if (count <= maxVisibleCount) {
                ImageView $imageView = newImageView();
                linkedList.add($imageView);
                addView($imageView);

            }
        }
    }
    //  void notifyItemInserted(int position) { }

    void notifyItemRemoved(int position) {
//        if (mAdapter != null) {
//            int count = mAdapter.getCount();
//            ImageView targetImageView = null;
//            if (position < count) {
//                ListIterator<ImageView> listIterator = linkedList.listIterator();
//                while (listIterator.hasNext()) {
//                    //先在前
//                    int index = listIterator.nextIndex();
//                    targetImageView = listIterator.next();
//                    if (index == position) {
//                        listIterator.remove();
//                        break;
//                    } else {
//                        targetImageView = null;
//                    }
//                }
//                if (targetImageView != null) {
//                    removeView(targetImageView);
//                }
//            }
//        }
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

            setMeasuredDimension(parentWidth, linkedList.get(0).getMeasuredHeight());
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

    public static abstract class Adapter {
        private AvatarSetView mAvatarSetView;

        public abstract void onBindAvatarItemView(ImageView imageView, int position);

        public abstract int getCount();

        public abstract int getMaxVisibleCount();


        public void notifyDataSetChanged() {
            if (mAvatarSetView != null) {
                mAvatarSetView.notifyDataSetChanged();
            }
        }

        public void notifyLastItemInserted() {
            if (mAvatarSetView != null) {
                mAvatarSetView.notifyLastItemInserted();
            }
        }
//        public void notifyItemInserted(int position) {
//            if (mAvatarSetView != null) {
//                mAvatarSetView.notifyItemInserted(position);
//            }
//        }

        public void notifyItemRemoved(int position) {
            if (mAvatarSetView != null) {
                mAvatarSetView.notifyItemRemoved(position);
            }
        }


    }
}
