package org.luyinbros.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.PopupWindowCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

/**
 * 在PopupWindow使用EditText的时候，无法实现长按复制粘贴功能(selection markers)
 * <p>
 * 建议:
 * 如果视图比较简单且无编辑框，可以使用PopupWindow
 * <p>
 * 弹框使用 DialogFragment
 * 有遮罩选择 DialogFragment
 * 有编辑框只能DialogFragment
 * Dialog没有缓存内存数据的作用，建议使用onAttach方法 从context/fragment 实现的接口对象
 */
public class UIPopupWindow {
    private PopupWindow mPopupWindow;
    private Context mContext;
    private Animation inAnimation = null;
    private Animation outAnimation = null;
    private boolean isClippingEnabled = false;


    private int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
    private PopupWindowGlobalLayoutListener mGlobalLayoutListener;
    private View mAnchorView;
    private int mVerticalGravity = VerticalGravity.NO_GRAVITY;
    private int mHorizontalGravity = HorizontalGravity.NO_GRAVITY;
    private int mOffsetX = 0;
    private int mOffsetY = 0;

    public UIPopupWindow(Context context) {
        this.mContext = context;
        mPopupWindow = new PopupWindow(context);
        mGlobalLayoutListener = new PopupWindowGlobalLayoutListener(this);
    }

    /**
     * 设置contentView
     *
     * @param view popupWindow content View
     */
    public void setContentView(View view) {
        removeGlobalLayoutListener();
        if (inAnimation != null) {
            inAnimation.cancel();
        }
        if (outAnimation != null) {
            outAnimation.cancel();
        }
        mPopupWindow.setContentView(view);
    }


    public void setTouchInterceptor(final View.OnTouchListener onTouchListener) {
        mPopupWindow.setTouchInterceptor(onTouchListener);
    }

    /**
     * 获取内容视图
     *
     * @return popupWindow content View
     */
    public View getContentView() {
        return mPopupWindow.getContentView();
    }

    /**
     * 根据屏幕位置显示，偏移量为0,0
     *
     * @param parent  a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param gravity the gravity which controls the placement of the popup window
     * @see #showAtLocation(View, int, int, int)
     */
    public void showAtLocation(View parent, int gravity) {
        showAtLocation(parent, gravity, 0, 0);
    }

    /**
     * example:Gravity.TOP | Gravity.LEFT 以屏幕左上角为坐标原点
     * Gravity.BOTTOM | Gravity.RIGHT 以屏幕右下角为坐标原点
     * Gravity.LEFT 以屏幕左侧，屏幕高度 1/2 处为坐标原点
     * x y 在Dialog 建议使用 anchor.getLocationInWindow(location)
     * 其余的可以使用anchor.getLocationOnScreen(location)
     *
     * @param parent  a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param gravity the gravity which controls the placement of the popup window
     * @param x       the popup's x location offset
     * @param y       he popup's y location offset
     * @see android.view.Gravity
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (!isShowing()) {
            mGlobalLayoutListener.isOnlyGetWH = true;
            mAnchorView = parent;
            mOffsetX = x;
            mOffsetY = y;
            addGlobalLayoutListener();
            mPopupWindow.showAtLocation(parent, gravity, x, y);
            showByAnim();
        }
    }

    /**
     * 根据AnchorView 位置显示
     *
     * @param anchorView        the view on which to pin the popup window
     * @param verticalGravity   垂直方向
     * @param horizontalGravity 水平方向
     */
    public void showAtAnchorView(@NonNull View anchorView, int verticalGravity, int horizontalGravity) {
        showAtAnchorView(anchorView, verticalGravity, horizontalGravity, 0, 0);
    }

    /**
     * 根据AnchorView 位置显示
     *
     * @param anchorView        the view on which to pin the popup window
     * @param verticalGravity   垂直方向
     * @param horizontalGravity 水平方向
     * @param x                 offset x
     * @param y                 offset y
     */
    public void showAtAnchorView(@NonNull View anchorView, int verticalGravity, int horizontalGravity, int x, int y) {
        if (getContentView() != null) {
            mGlobalLayoutListener.isOnlyGetWH = false;
            mAnchorView = anchorView;
            mOffsetX = x;
            mOffsetY = y;
            mVerticalGravity = verticalGravity;
            mHorizontalGravity = horizontalGravity;
            final View contentView = getContentView();
            addGlobalLayoutListener();
            contentView.measure(makeDropDownMeasureSpec(mPopupWindow.getWidth()), makeDropDownMeasureSpec(mPopupWindow.getHeight()));
            final int measuredWidth = contentView.getMeasuredWidth();
            final int measuredHeight = contentView.getMeasuredHeight();

            if (!isClippingEnabled) {
                final int[] anchorLocation = new int[2];
                anchorView.getLocationInWindow(anchorLocation);
                x += anchorLocation[0];
                y += anchorLocation[1] + anchorView.getHeight();
            }
            x = calculateX(anchorView, horizontalGravity, measuredWidth, x);
            y = calculateY(anchorView, verticalGravity, measuredHeight, y);
            if (isClippingEnabled) {
                PopupWindowCompat.showAsDropDown(mPopupWindow, anchorView, x, y, Gravity.NO_GRAVITY);
                showByAnim();
            } else {
                showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
            }
        }
    }


    /**
     * 是否允许超过边界
     *
     * @param enabled true为允许
     * @see #update()
     */
    public void setClippingEnabled(boolean enabled) {
        isClippingEnabled = enabled;
        mPopupWindow.setClippingEnabled(enabled);
    }

    /**
     * popupWindow是否显示
     */
    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        dismiss(true);
    }

    /**
     * 隐藏
     *
     * @param isAnim 是否需要动画
     */
    public void dismiss(boolean isAnim) {
        if (getContentView() == null) {
            return;
        }
        if (mPopupWindow.isShowing()) {
            if (isAnim) {
                if (inAnimation != null) {
                    inAnimation.cancel();
                }
                if (outAnimation != null) {
                    getContentView().startAnimation(outAnimation);
                }
            } else {
                if (inAnimation != null) {
                    inAnimation.cancel();
                }
                if (outAnimation != null) {
                    outAnimation.cancel();
                }
                mPopupWindow.dismiss();
            }
            removeGlobalLayoutListener();
        }

    }

    public void setSoftInputMode(int mode) {
        mPopupWindow.setSoftInputMode(mode);
    }

    public void setAnimationStyle(int animationStyle) {
        mPopupWindow.setAnimationStyle(animationStyle);
    }

    public void setContentAnimation(int inAnim, int outAnim) {
        this.inAnimation = AnimationUtils.loadAnimation(mContext, inAnim);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.outAnimation = AnimationUtils.loadAnimation(mContext, outAnim);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPopupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 设置是否获取焦点
     *
     * @param focusable true 允许当前获取焦点
     */
    public void setFocusable(boolean focusable) {
        mPopupWindow.setFocusable(focusable);
    }

    /**
     * 设置是否允许外部点击
     *
     * @param touchable true点击外部隐藏popupWindow
     */
    public void setOutsideTouchable(boolean touchable) {
        mPopupWindow.setOutsideTouchable(touchable);
    }


    /**
     * 设置popupWindow 宽度
     *
     * @param width popupWindow 宽度
     */
    public void setWidth(int width) {
        mPopupWindow.setWidth(width);
    }

    /**
     * 设置popupWindow 高度
     *
     * @param height popupWindow 高度
     */
    public void setHeight(int height) {
        mPopupWindow.setHeight(height);
    }

    /**
     * 更新配置参数
     */
    public void update() {
        mPopupWindow.update();
    }

    /**
     * 设置遮罩背景
     *
     * @param background 背景视图
     */
    public void setBackgroundDrawable(Drawable background) {
        mPopupWindow.setBackgroundDrawable(background);
    }


    private void showByAnim() {
        if (getContentView() != null) {
            if (inAnimation != null) {
                getContentView().startAnimation(inAnimation);
            }
        }
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), getDropDownMeasureSpecMode(measureSpec));
    }

    private static int getDropDownMeasureSpecMode(int measureSpec) {
        switch (measureSpec) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return View.MeasureSpec.UNSPECIFIED;
            default:
                return View.MeasureSpec.EXACTLY;
        }
    }


    /**
     * 根据垂直gravity计算y偏移
     */
    private int calculateY(View anchor, int verticalGravity, int measuredHeight, int y) {
        switch (verticalGravity) {
            case VerticalGravity.ABOVE:
                y -= measuredHeight + anchor.getHeight();
                break;
            case VerticalGravity.ALIGN_BOTTOM:
                y -= measuredHeight;
                break;
            case VerticalGravity.CENTER:
                y -= anchor.getHeight() / 2 + measuredHeight / 2;
                break;
            case VerticalGravity.ALIGN_TOP:
                y -= anchor.getHeight();
                break;
            case VerticalGravity.BELOW:
            default:
                return y;
        }
        return y;
    }

    /**
     * 根据水平gravity计算x偏移
     */
    private int calculateX(View anchor, int horizontalGravity, int measuredWidth, int x) {
        switch (horizontalGravity) {
            case HorizontalGravity.LEFT:
                x -= measuredWidth;
                break;
            case HorizontalGravity.ALIGN_RIGHT:
                x -= measuredWidth - anchor.getWidth();
                break;
            case HorizontalGravity.CENTER:
                x += anchor.getWidth() / 2 - measuredWidth / 2;
                break;

            case HorizontalGravity.RIGHT:
                x += anchor.getWidth();
                break;
            case HorizontalGravity.ALIGN_LEFT:
            default:
                return x;
        }

        return x;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void removeGlobalLayoutListener() {
        if (getContentView() != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
            } else {
                getContentView().getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
            }
        }
    }

    private void addGlobalLayoutListener() {
        if (getContentView() != null) {
            getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

    }

    private void updateLocation(int width, int height, @NonNull View anchor,
                                final int verticalGravity,
                                int horizontalGravity,
                                int x, int y) {
        x = calculateX(anchor, horizontalGravity, width, x);
        y = calculateY(anchor, verticalGravity, height, y);
        mPopupWindow.update(anchor, x, y, width, height);
    }


    private static class PopupWindowGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private UIPopupWindow mPopupWindow;
        private boolean isOnlyGetWH = true;


        private PopupWindowGlobalLayoutListener(UIPopupWindow popupWindow) {
            this.mPopupWindow = popupWindow;
        }

        @Override
        public void onGlobalLayout() {
            View contentView = mPopupWindow.getContentView();
            if (contentView != null) {
                mPopupWindow.mWidth = contentView.getWidth();
                mPopupWindow.mHeight = contentView.getHeight();
                if (isOnlyGetWH) {
                    mPopupWindow.removeGlobalLayoutListener();
                    return;
                }
                if (mPopupWindow.mAnchorView != null) {
                    mPopupWindow.updateLocation(mPopupWindow.mWidth, mPopupWindow.mHeight,
                            mPopupWindow.mAnchorView,
                            mPopupWindow.mVerticalGravity,
                            mPopupWindow.mHorizontalGravity,
                            mPopupWindow.mOffsetX,
                            mPopupWindow.mOffsetY);
                }
                mPopupWindow.removeGlobalLayoutListener();
            }
        }

    }


    public static class VerticalGravity {
        public static final int NO_GRAVITY = -1;
        public static final int CENTER = 0;
        public static final int ABOVE = 1;
        public static final int BELOW = 2;
        public static final int ALIGN_TOP = 3;
        public static final int ALIGN_BOTTOM = 4;
    }

    public static class HorizontalGravity {
        public static final int NO_GRAVITY = -1;
        public static final int CENTER = 0;
        public static final int LEFT = 1;
        public static final int RIGHT = 2;
        public static final int ALIGN_LEFT = 3;
        public static final int ALIGN_RIGHT = 4;

    }
}
