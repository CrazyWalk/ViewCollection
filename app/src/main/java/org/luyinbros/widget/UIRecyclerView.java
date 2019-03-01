package org.luyinbros.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class UIRecyclerView extends RecyclerView {

    public UIRecyclerView(@NonNull Context context) {
        super(context);
    }

    public UIRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UIRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public static class SimpleLinearItemDecoration extends RecyclerView.ItemDecoration {
        private int paddingLeft;
        private int paddingRight;
        private int paddingBottom;
        private int paddingTop;
        private int interval;
        private boolean allowOffsetsLast = false;

        public SimpleLinearItemDecoration() {
            this(0, 0);
        }

        /**
         * 线性装饰者构造器
         *
         * @param interval item之间间隙 单位px
         */
        public SimpleLinearItemDecoration(@Px int interval) {
            this(interval, 0);
        }

        /**
         * 线性装饰者构造器
         *
         * @param interval item之间间隙 单位px
         * @param padding  recyclerView padding  单位px
         */
        public SimpleLinearItemDecoration(@Px int interval, @Px int padding) {
            this.interval = interval;
            this.paddingLeft = padding;
            this.paddingRight = padding;
            this.paddingBottom = padding;
            this.paddingTop = padding;
        }


        public SimpleLinearItemDecoration setAllowOffsetsLast(boolean allowOffsetsLast) {
            this.allowOffsetsLast = allowOffsetsLast;
            return this;
        }

        /**
         * 设置item间隙
         *
         * @param interval 间隙值
         */
        public SimpleLinearItemDecoration setInterval(@Px int interval) {
            this.interval = interval;
            return this;
        }

        public SimpleLinearItemDecoration setPadding(@Px int padding) {
            return setPadding(padding, padding, padding, padding);
        }

        /**
         * 设置padding 这个padding是参与滚动的，与view.setPadding是不同的
         *
         * @param paddingLeft   左填充
         * @param paddingTop    顶部填充
         * @param paddingRight  右边填充
         * @param paddingBottom 下填充
         */
        public SimpleLinearItemDecoration setPadding(@Px int paddingLeft, @Px int paddingTop, @Px int paddingRight, @Px int paddingBottom) {
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.paddingBottom = paddingBottom;
            this.paddingTop = paddingTop;
            return this;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (isEnable(parent)) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    int itemPosition = parent.getChildAdapterPosition(view);
                    outRect.top = paddingTop;
                    outRect.bottom = paddingBottom;
                    if (itemPosition == 0) {
                        outRect.left = paddingLeft;
                        outRect.right = 0;
                    } else if (itemPosition == parent.getAdapter().getItemCount() - 1) {
                        outRect.left = interval;
                        outRect.right = paddingRight;
                    } else {
                        outRect.left = interval;
                        outRect.right = 0;
                    }
                } else {
                    if (allowOffsetsLast) {
                        if (parent.getAdapter().getItemCount() == 1) {
                            outRect.set(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        } else {
                            int itemPosition = parent.getChildAdapterPosition(view);
                            if (itemPosition == 0) {
                                outRect.set(paddingLeft, paddingTop, paddingRight, 0);
                            } else if (itemPosition == parent.getAdapter().getItemCount() - 1) {
                                outRect.set(paddingLeft, interval, paddingRight, paddingBottom);
                            } else {
                                outRect.set(paddingLeft, interval, paddingRight, 0);
                            }
                        }
                    } else {
                        if (parent.getAdapter().getItemCount() == 1) {
                            outRect.set(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        } else {
                            int itemPosition = parent.getChildAdapterPosition(view);
                            if (itemPosition == 0) {
                                outRect.set(paddingLeft, paddingTop, paddingRight, 0);
                            } else if (itemPosition != parent.getAdapter().getItemCount() - 1) {
                                outRect.set(paddingLeft, interval, paddingRight, 0);
                            }
                        }
                    }
                }
            }

        }


        private boolean isEnable(RecyclerView parent) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            return layoutManager instanceof LinearLayoutManager && parent.getAdapter() != null
                    && parent.getAdapter().getItemCount() != 0;
        }
    }

    public static class SimpleGridItemDecoration extends RecyclerView.ItemDecoration {
        private int paddingLeft;
        private int paddingRight;
        private int paddingBottom;
        private int paddingTop;
        private int rowInterval;
        private int columnInterval;
        private boolean allowOffsetsLast = false;

        private SimpleGridItemDecoration() {
        }

        public SimpleGridItemDecoration setPadding(int padding) {
            return setPadding(padding, padding, padding, padding);
        }

        public SimpleGridItemDecoration setPadding(int paddingLeft, int paddingRight, int paddingBottom, int paddingTop) {
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
            this.paddingBottom = paddingBottom;
            this.paddingTop = paddingTop;
            return this;
        }

        public SimpleGridItemDecoration setInterval(int interval) {
            this.rowInterval = interval;
            this.columnInterval = interval;
            return this;
        }

        public SimpleGridItemDecoration setRowInterval(int interval) {
            this.rowInterval = interval;
            return this;
        }

        public SimpleGridItemDecoration setColumnInterval(int interval) {
            this.columnInterval = interval;
            return this;
        }

        public SimpleGridItemDecoration setAllowOffsetsLast(boolean allowOffsetsLast) {
            this.allowOffsetsLast = allowOffsetsLast;
            return this;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (isEnable(parent)) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
                final int itemPosition = parent.getChildAdapterPosition(view);
                final int totalCount = parent.getAdapter().getItemCount();
                if (isOffsets(itemPosition, totalCount)) {
                    final int spanCount = gridLayoutManager.getSpanCount();
                    final int totalRow = totalCount % spanCount != 0 ? totalCount / spanCount + 1 : totalCount / spanCount;

                    int row = itemPosition / spanCount;
                    if (spanCount == 1) {
                        outRect.left = paddingLeft;
                        outRect.right = paddingRight;
                        if (row == 0) {
                            outRect.top = paddingTop;
                            outRect.bottom = 0;
                        } else if (row == totalRow - 1) {
                            outRect.top = rowInterval;
                            outRect.bottom = paddingBottom;
                        } else {
                            outRect.top = rowInterval;
                            outRect.bottom = 0;
                        }
                    } else if (spanCount > 1) {

                        int column = itemPosition % spanCount; // item column
                        if (column == 0) {
                            outRect.left = paddingLeft;
                            outRect.right = (column + 1) * columnInterval / spanCount;
                        } else if (column == spanCount - 1) {
                            outRect.left = columnInterval - column * columnInterval / spanCount;
                            outRect.right = paddingRight;
                        } else {
                            outRect.left = columnInterval - column * columnInterval / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                            outRect.right = (column + 1) * columnInterval / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                        }

                        if (row == 0) {
                            outRect.top = paddingTop;
                            outRect.bottom = 0;
                        } else if (row == totalRow - 1) {
                            outRect.top = rowInterval;
                            outRect.bottom = paddingBottom;
                        } else {
                            outRect.top = rowInterval;
                            outRect.bottom = 0;
                        }
                    }
                }
            }

        }

        private boolean isOffsets(int position, int totalCount) {
            if (allowOffsetsLast) {
                return true;
            } else {
                return position < totalCount - 1;
            }
        }

        private boolean isEnable(RecyclerView parent) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            return layoutManager != null && layoutManager instanceof GridLayoutManager
                    && parent.getAdapter() != null && parent.getAdapter().getItemCount() != 0 &&
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup() != null;
        }
    }


}
