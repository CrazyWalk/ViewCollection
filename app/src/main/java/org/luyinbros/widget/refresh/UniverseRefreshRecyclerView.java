package org.luyinbros.widget.refresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.luyinbros.widget.recyclerview.CellHolder;
import org.luyinbros.widget.recyclerview.CellSpan;
import org.luyinbros.widget.recyclerview.RecyclerViewCell;
import org.luyinbros.widget.recyclerview.ViewCellAdapter;
import org.luyinbros.widget.status.DefaultStatusLayoutController;
import org.luyinbros.widget.status.OnPageRefreshListener;
import org.luyinbros.widget.status.StatusLayoutController;

public class UniverseRefreshRecyclerView extends RefreshLayout implements RefreshListController {
    private RecyclerView mRecyclerView;
    private StatusLayoutController mStatusController;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LoadMoreRefreshController mLoadMoreRefreshController;
    private OnLoadMoreRefreshListener mOnLoadMoreRefreshListener;
    private boolean isLoadMoreEnable = false;

    public UniverseRefreshRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public UniverseRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setClipChildren(false);
        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        frameLayout.addView(mRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mStatusController = new DefaultStatusLayoutController(mRecyclerView);
    }

    @Override
    public void setOnLoadMoreRefreshListener(OnLoadMoreRefreshListener listener) {
        this.mOnLoadMoreRefreshListener = listener;
        if (mLoadMoreRefreshController != null) {
            mLoadMoreRefreshController.setOnLoadMoreRefreshListener(listener);
        }
    }

    @Override
    public int getLoadMoreStatus() {
        return mLoadMoreRefreshController == null ?
                LOAD_MORE_STATUS_INVALID : mLoadMoreRefreshController.getLoadMoreStatus();
    }


    @Override
    public void setLoadMoreStatus(int status) {
        if (mLoadMoreRefreshController != null) {
            mLoadMoreRefreshController.setLoadMoreStatus(status);
        }
    }

    @Override
    public void setLoadMoreEnable(boolean enable) {
        isLoadMoreEnable = enable;
        if (mAdapter instanceof ViewCellAdapter) {
            if (isLoadMoreEnable) {
                if (mLoadMoreRefreshController == null) {
                    mLoadMoreRefreshController = new LoadMoreCell();
                    mLoadMoreRefreshController.setOnLoadMoreRefreshListener(mOnLoadMoreRefreshListener);
                }
                ((ViewCellAdapter) mAdapter).setBottomCell((RecyclerViewCell) mLoadMoreRefreshController);
            } else {
                setLoadMoreStatus(LOAD_MORE_STATUS_INVALID);
                ((ViewCellAdapter) mAdapter).removeBottomCell();
            }
        }
    }

    @Override
    public boolean isLoadMoreEnable() {
        return isLoadMoreEnable;
    }

    @Override
    public boolean isLoadMoreRefreshing() {
        return getLoadMoreStatus() == LOAD_MORE_STATUS_REFRESH;
    }

    @Override
    public void addStatusPage(StatusPage statusView) {
        mStatusController.addStatusPage(statusView);
    }

    @Override
    public void registerStatusPageCreator(StatusPageCreator statusViewCreator) {
        mStatusController.registerStatusPageCreator(statusViewCreator);
    }

    @Override
    public void showEmptyPage() {
        mStatusController.showEmptyPage();
    }

    @Override
    public void showFailurePage() {
        mStatusController.showFailurePage();
    }

    @Override
    public void showRefreshPage() {
        mStatusController.showRefreshPage();
    }

    @Override
    public void showStatusPage(int status) {
        mStatusController.showStatusPage(status);
    }

    @Override
    public void showContentView() {
        mStatusController.showContentView();
    }

    @Override
    public void setOnPageRefreshListener(OnPageRefreshListener onPageRefreshListener) {
        mStatusController.setOnPageRefreshListener(onPageRefreshListener);
    }

    @Override
    public int getPageStatus() {
        return mStatusController.getPageStatus();
    }

    /**
     * 为recyclerView设置适配器
     *
     * @param adapter 当前适配器
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
        setLayoutManager(mLayoutManager);
        setLoadMoreEnable(isLoadMoreEnable);
        setOnLoadMoreRefreshListener(mOnLoadMoreRefreshListener);
    }

    /**
     * 获取recyclerView适配器
     *
     * @return 如果recyclerView有配置，返回
     */
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置recyclerView布局管理器
     *
     * @param layout 当前管理器
     */
    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        this.mLayoutManager = layout;
        if (!(mAdapter instanceof ViewCellAdapter)) {
            mRecyclerView.setLayoutManager(layout);
        } else {
            ((ViewCellAdapter) mAdapter).setLayoutManager(mLayoutManager);
        }

    }

    /**
     * 获取recyclerView布局管理
     *
     * @return 返回当前recyclerView的布局管理
     */
    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    /**
     * 添加 recyclerView 装饰者
     *
     * @param decor 当前装饰者
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }


    public RecyclerView.ViewHolder findViewHolderForAdapterPosition(int position) {
        return mRecyclerView.findViewHolderForAdapterPosition(position);
    }

    /**
     * 移除 recyclerView 装饰者
     *
     * @param decor 当前装饰者
     */
    public void removeItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.removeItemDecoration(decor);
    }

    /**
     * 添加滚动监听事件
     *
     * @param listener 当前监听事件
     */
    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        mRecyclerView.addOnScrollListener(listener);
    }

    /**
     * 移除滚动监听事件
     *
     * @param listener 当前需要移除的监听事件
     */
    public void removeOnScrollListener(RecyclerView.OnScrollListener listener) {
        mRecyclerView.removeOnScrollListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    /**
     * 滚动到指定位置
     *
     * @param position 滚动的重点
     */
    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);

    }

    /**
     * 平滑的滚动到指定位置
     *
     * @param position 当前位置
     */
    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        mRecyclerView.stopScroll();
    }

    @Override
    public int getItemCount() {
        if (mAdapter == null) {
            return 0;
        } else {
            if (mAdapter instanceof ViewCellAdapter) {
                return ((ViewCellAdapter) mAdapter).getCellItemCount();
            } else {
                return mAdapter.getItemCount();
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private static class LoadMoreCell extends RecyclerViewCell<LoadMoreCell.ItemHolder> implements LoadMoreRefreshController, CellSpan {
        private int mStatus;
        private OnLoadMoreRefreshListener onLoadMoreRefreshListener;

        private LoadMoreCell() {
            mStatus = LOAD_MORE_STATUS_INVALID;
        }

        @Override
        public void setOnLoadMoreRefreshListener(OnLoadMoreRefreshListener onLoadMoreRefreshListener) {
            this.onLoadMoreRefreshListener = onLoadMoreRefreshListener;
        }

        @Override
        public ItemHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            final DefaultLoadMoreView defaultLoadMoreView = new DefaultLoadMoreView(parent.getContext());
            defaultLoadMoreView.setStatus(mStatus);
            defaultLoadMoreView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onLoadMoreRefreshListener != null && mStatus == LOAD_MORE_STATUS_FAILURE) {
                        mStatus = LOAD_MORE_STATUS_REFRESH;
                        defaultLoadMoreView.setStatus(mStatus);
                        onLoadMoreRefreshListener.onLoadMoreRefresh();
                    }
                }
            });
            return new ItemHolder(defaultLoadMoreView);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.mDefaultLoadMoreView.setStatus(mStatus);
        }

        @Override
        public synchronized void setLoadMoreStatus(int status) {
            if (mStatus != status) {
                if (status == LOAD_MORE_STATUS_REFRESH) {
                    if (onLoadMoreRefreshListener != null) {
                        onLoadMoreRefreshListener.onLoadMoreRefresh();
                    }
                }
                this.mStatus = status;
                notifyDataSetChanged();
            }
        }

        @Override
        public void setLoadMoreEnable(boolean enable) {
            //no support
        }

        @Override
        public boolean isLoadMoreEnable() {
            //no support
            return true;
        }

        @Override
        public boolean isLoadMoreRefreshing() {
            //no support
            return false;
        }

        @Override
        public int getLoadMoreStatus() {
            return mStatus;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            recyclerView.addOnScrollListener(mOnScrollChangeListener);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            recyclerView.removeOnScrollListener(mOnScrollChangeListener);
        }

        @Override
        public void onDetachedFromAdapter() {
            super.onDetachedFromAdapter();
            mStatus = LOAD_MORE_STATUS_INVALID;
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        static class ItemHolder extends CellHolder {
            private DefaultLoadMoreView mDefaultLoadMoreView;

            ItemHolder(DefaultLoadMoreView defaultLoadMoreView) {
                super(defaultLoadMoreView);
                mDefaultLoadMoreView = (DefaultLoadMoreView) itemView;
            }
        }

        private RecyclerView.OnScrollListener mOnScrollChangeListener = new RecyclerView.OnScrollListener() {
            private int lastVisibleItemPosition;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    return;
                }
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    final int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVisibleItemPosition = findLastPosition(lastPositions);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (mStatus == LoadMoreRefreshController.LOAD_MORE_STATUS_IDLE) {
                    final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager == null) {
                        return;
                    }
                    if (layoutManager.getChildCount() > 0
                            && newState == RecyclerView.SCROLL_STATE_IDLE
                            && lastVisibleItemPosition == layoutManager.getItemCount() - 1) {
                        setLoadMoreStatus(LoadMoreRefreshController.LOAD_MORE_STATUS_REFRESH);
                    }
                }

            }

            private int findLastPosition(int[] lastPositions) {
                int max = lastPositions[0];
                for (int value : lastPositions) {
                    if (value > max) {
                        max = value;
                    }
                }
                return max;
            }
        };

        @Override
        public int getSpanSize(int position, int spanTotalCount) {
            return spanTotalCount;
        }

    }
}
