package org.luyinbros.widget.self;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;


public class BannerView extends FrameLayout implements PagerView {
    private static final String TAG = "BannerView";
    private boolean mIsAutoScrollEnable = false;
    private boolean mIsInfinite = false;

    private ViewPager mViewPager;
    private InfiniteAdapter mPagerAdapter;
    private final int mScrollDelay = 2500;
    private final Handler mAutoScrollHandler = new Handler();
    private final Runnable mAutoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int totalCount = mPagerAdapter.getCount();
            if (mIsInfinite && totalCount > 1) {
                int currentPosition = mViewPager.getCurrentItem();
                if (currentPosition + 1 < totalCount) {
                    mViewPager.setCurrentItem(currentPosition + 1, true);
                } else {
                    setAdapter(mPagerAdapter.mAdapter);
                }
                mAutoScrollHandler.postDelayed(this, mScrollDelay);
            }

        }
    };
    private Indicator mIndicator;
    private WrapperPagerChangeListener mIndicatorPagerChangeListener;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewPager = new InnerViewPager(context);
        mViewPager.setOffscreenPageLimit(4);
        ((InnerViewPager) mViewPager).setBannerView(this);
        mPagerAdapter = new InfiniteAdapter(this);
        addView(mViewPager, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        try {
//            Field field = ViewPager.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(context,
//                    new AccelerateInterpolator());
//            field.set(mViewPager, scroller);
//            scroller.setmDuration(1500);
//        } catch (Exception e) {
//            //no Action
//        }
    }


    public void setIndicator(Indicator indicator) {
        if (mIndicator != null) {
            removeView((View) mIndicator);
            mIndicator.setPagerView(null);
            mViewPager.removeOnPageChangeListener(mIndicatorPagerChangeListener);
        }
        if (indicator instanceof View) {
            this.mIndicator = indicator;
            mIndicator.setPagerView(this);
            mIndicatorPagerChangeListener = new WrapperPagerChangeListener(mIndicator);
            mViewPager.addOnPageChangeListener(mIndicatorPagerChangeListener);
            addView((View) mIndicator);
        }

    }

    public void setAdapter(Adapter adapter) {
        stopAutoScroll();
        mViewPager.setAdapter(null);
        mPagerAdapter.mAdapter = adapter;
        if (mPagerAdapter.mAdapter != null) {
            mPagerAdapter.mAdapter.bannerView = this;
        }
        mViewPager.setAdapter(mPagerAdapter);
        if (mIndicator != null) {
            mIndicator.onSetDataChanged();
        }
        startAutoScroll();
    }

    public void setInfinite(boolean infinite) {
        stopAutoScroll();
        this.mIsInfinite = infinite;
        setAdapter(mPagerAdapter.mAdapter);
        startAutoScroll();
    }

    public void setAutoScrollEnable(boolean autoScrollEnable) {
        stopAutoScroll();
        mIsAutoScrollEnable = autoScrollEnable;
        startAutoScroll();
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
        super.setClipChildren(clipChildren);
        if (mViewPager != null) {
            mViewPager.setClipChildren(clipChildren);
        }
    }

    public void setMarginViewPager(int marginLeft, int marginRight) {
        FrameLayout.LayoutParams layoutParams= (FrameLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.leftMargin=marginLeft;
        layoutParams.rightMargin=marginRight;
        mViewPager.setLayoutParams(layoutParams);
    }

    public void setPageMargin(int margin) {
        mViewPager.setPageMargin(margin);
    }

    public void setOffscreenPageLimit(int limit) {
        mViewPager.setOffscreenPageLimit(limit);
    }

    public boolean isAutoScrollEnable() {
        return mIsAutoScrollEnable;
    }

    @Override
    public int getItemCount() {
        return mPagerAdapter.getRealCount();
    }

    private void startAutoScroll() {
        mAutoScrollHandler.removeCallbacks(mAutoScrollRunnable);
        int count = mPagerAdapter.getCount();
        if (count > 1 && isAutoScrollEnable() && mIsInfinite) {
            mAutoScrollHandler.postDelayed(mAutoScrollRunnable, mScrollDelay);
        }
    }


    private void stopAutoScroll() {
        mAutoScrollHandler.removeCallbacks(mAutoScrollRunnable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoScroll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoScroll();
    }

    public interface Indicator extends ViewPager.OnPageChangeListener {
        int SCROLL_STATE_IDLE = ViewPager.SCROLL_STATE_IDLE;
        int SCROLL_STATE_DRAGGING = ViewPager.SCROLL_STATE_DRAGGING;
        int SCROLL_STATE_SETTLING = ViewPager.SCROLL_STATE_SETTLING;

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        /**
         * @see #SCROLL_STATE_IDLE
         * @see #SCROLL_STATE_DRAGGING
         * @see #SCROLL_STATE_SETTLING
         */
        void onPageScrollStateChanged(int state);

        void setPagerView(@Nullable PagerView pagerView);

        void onSetDataChanged();
    }

    public static abstract class Adapter<VH extends ViewHolder> {
        private BannerView bannerView;

        public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(@NonNull VH holder, int position);

        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        public void notifyDataSetChanged() {
            if (bannerView != null) {
                Adapter currentAdapter = bannerView.mPagerAdapter.mAdapter;
                if (currentAdapter == this) {
                    bannerView.setAdapter(this);
                }
            }
        }
    }

    public static class ViewHolder {
        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

    }

    private static class InfiniteAdapter extends PagerAdapter {
        private final static int VIRTUAL_ITEM_COUNT = 10_000_000;
        private Adapter mAdapter;
        private BannerView bannerView;
        private int mChildCount = 0;

        private InfiniteAdapter(BannerView bannerView) {
            this.bannerView = bannerView;
        }

        @Override
        public int getCount() {
            if (mAdapter == null) {
                return 0;
            }
            int count = getRealCount();
            if (count <= 1 || !bannerView.mIsInfinite) {
                return count;
            }
            return VIRTUAL_ITEM_COUNT;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final int virtualPosition = getVirtualPosition(position);
            int viewType = mAdapter.getItemViewType(virtualPosition);
            ViewHolder holder = mAdapter.onCreateViewHolder(container, viewType);
            mAdapter.onBindViewHolder(holder, virtualPosition);
            container.addView(holder.itemView);
            return holder;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof ViewHolder) {
                ViewHolder holder = (ViewHolder) object;
                container.removeView(holder.itemView);
            }

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            if (o instanceof ViewHolder) {
                ViewHolder holder = (ViewHolder) o;
                return holder.itemView == view;
            }
            return false;
        }

        private int getVirtualPosition(final int realPosition) {
            int realCount = getRealCount();
            if (realCount == 0) {
                return 0;
            }
            return realPosition % realCount;
        }

        private int getRealCount() {
            if (mAdapter == null) {
                return 0;
            }
            return mAdapter.getItemCount();
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public final int getItemPosition(@NonNull Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }
    }

    private class WrapperPagerChangeListener implements ViewPager.OnPageChangeListener {
        private ViewPager.OnPageChangeListener onPageChangeListener;

        public WrapperPagerChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
            this.onPageChangeListener = onPageChangeListener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrolled(mPagerAdapter.getVirtualPosition(position), positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(mPagerAdapter.getVirtualPosition(position));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    }

    private class FixedSpeedScroller extends Scroller {
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setmDuration(int time) {
            mDuration = time;
        }

        public int getmDuration() {
            return mDuration;
        }
    }

    private static class InnerViewPager extends ViewPager {
        private BannerView bannerView;

        public InnerViewPager(@NonNull Context context) {
            super(context);
        }

        public void setBannerView(BannerView bannerView) {
            this.bannerView = bannerView;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (bannerView != null) {
                        bannerView.stopAutoScroll();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (bannerView != null) {
                        bannerView.startAutoScroll();
                    }
                    break;
            }
            return super.onTouchEvent(event);
        }
    }
}
