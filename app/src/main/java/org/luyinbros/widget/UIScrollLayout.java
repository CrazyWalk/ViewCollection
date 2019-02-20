package org.luyinbros.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

public class UIScrollLayout extends NestedScrollView {
    private float startDragY;
    // private SpringAnimation springAnim;
    private ScrollViewListener scrollViewListener;


    public UIScrollLayout(Context context) {
        this(context, null);
    }

    public UIScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //    springAnim = new SpringAnimation(this, SpringAnimation.TRANSLATION_Y, 0);
        //刚度 默认1200 值越大回弹的速度越快
        //   springAnim.getSpring().setStiffness(1000.0f);
        //阻尼 默认0.5 值越小，回弹之后来回的次数越多
        //   springAnim.getSpring().setDampingRatio(2f);
       // setOverScrollMode(OVER_SCROLL_NEVER);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                if (getScrollY() <= 0) {
//                    //顶部下拉
//                    if (startDragY == 0) {
//                        startDragY = e.getRawY();
//                    }
//                    if (e.getRawY() - startDragY >= 0) {
//                        setTranslationY((e.getRawY() - startDragY) / 3);
//                        if (view != null) {
//                            float alpha = (float) (e.getRawY() - startDragY) / 3 / px(150);
//                            if (alpha < 0.6) {
//                                view.setAlpha(alpha);
//                            } else {
//                                view.setAlpha((float) 0.6);
//                            }
//                        }
//                        return true;
//                    } else {
//                        startDragY = 0;
//                        springAnim.cancel();
//                        setTranslationY(0);
//                    }
//
//                } else if ((getScrollY() + getHeight()) >= getChildAt(0).getMeasuredHeight()) {
//                    //底部上拉
//                    if (startDragY == 0) {
//                        startDragY = e.getRawY();
//                    }
//                    if (e.getRawY() - startDragY <= 0) {
//                        setTranslationY((e.getRawY() - startDragY) / 3);
//                        return true;
//                    } else {
//                        startDragY = 0;
//                        springAnim.cancel();
//                        setTranslationY(0);
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                if (getTranslationY() != 0) {
//                    springAnim.start();
//                }
//                startDragY = 0;
//                break;
//        }
//
//        return super.onTouchEvent(e);
//
//    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }


    private int px(int dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }


    public interface ScrollViewListener {
        void onScrollChanged(UIScrollLayout scrollView, int x, int y, int oldx, int oldy);
    }

}
