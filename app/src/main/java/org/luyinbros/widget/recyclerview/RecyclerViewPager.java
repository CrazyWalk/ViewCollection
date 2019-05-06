package org.luyinbros.widget.recyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class RecyclerViewPager extends RecyclerView {
    public RecyclerViewPager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        ScrollSpeedLinearLayoutManger scrollSpeedLinearLayoutManger = new ScrollSpeedLinearLayoutManger(context, LinearLayoutManager.HORIZONTAL, false);
        scrollSpeedLinearLayoutManger.setSpeedSlow();
        setLayoutManager(scrollSpeedLinearLayoutManger);
        LinearSnapHelper mLinearSnapHelper = new LeftSnapHelper();
        mLinearSnapHelper.attachToRecyclerView(this);
    }



    private class ScrollSpeedLinearLayoutManger extends LinearLayoutManager {
        private float MILLISECONDS_PER_INCH = 0.03f;
        private Context context;
        private LinearSmoothScroller linearSmoothScroller;

        private ScrollSpeedLinearLayoutManger(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.context = context;
            linearSmoothScroller =
                    new LinearSmoothScroller(context) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return ScrollSpeedLinearLayoutManger.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        @Override
                        protected float calculateSpeedPerPixel
                                (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                            //返回滑动一个pixel需要多少毫秒
                        }

                        @Override
                        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
                        }
                    };
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }


        public void setSpeedSlow() {
            //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
            //0.3f是自己估摸的一个值，可以根据不同需求自己修改
            MILLISECONDS_PER_INCH = context.getResources().getDisplayMetrics().density * 0.8f;
        }

        public void setSpeedFast() {
            MILLISECONDS_PER_INCH = context.getResources().getDisplayMetrics().density * 0.03f;
        }
    }

    private class LeftSnapHelper extends LinearSnapHelper {
        private OrientationHelper mHorizontalHelper;

        /**
         * 当拖拽或滑动结束时会回调该方法,该方法返回的是一个长度为2的数组,out[0]表示横轴,x[1]表示纵轴,这两个值就是你需要修正的位置的偏移量
         *
         * @param layoutManager
         * @param targetView
         * @return
         */
        @Override
        public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager layoutManager, View targetView) {
            //注:由于是横向滚动,在这里我们只考虑横轴的值
            int[] out = new int[2];
            if (layoutManager.canScrollHorizontally()) {
                out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
            } else {
                out[0] = 0;
            }
            return out;
        }

        /**
         * 这个方法是计算偏移量
         *
         * @param targetView
         * @param helper
         * @return
         */
        private int distanceToStart(View targetView, OrientationHelper helper) {
            return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
        }

        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            return findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }

        /**
         * 找到第一个显示的view
         *
         * @param layoutManager
         * @param helper
         * @return
         */
        private View findStartView(RecyclerView.LayoutManager layoutManager,
                                   OrientationHelper helper) {
            if (layoutManager instanceof LinearLayoutManager) {
                int firstChild = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int lastChild = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                if (firstChild == RecyclerView.NO_POSITION) {
                    return null;
                }

                //这是为了解决当翻到最后一页的时候，最后一个Item不能完整显示的问题
                if (lastChild == layoutManager.getItemCount() - 1) {
                    return layoutManager.findViewByPosition(lastChild);
                }
                View child = layoutManager.findViewByPosition(firstChild);

                //得到此时需要左对齐显示的条目
                if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                        && helper.getDecoratedEnd(child) > 0) {
                    return child;
                } else {
                    return layoutManager.findViewByPosition(firstChild + 1);
                }
            }
            return super.findSnapView(layoutManager);
        }

        /**
         * 获取视图的方向
         *
         * @param layoutManager
         * @return
         */
        private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
            if (mHorizontalHelper == null) {
                mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
            }
            return mHorizontalHelper;
        }
    }
}
