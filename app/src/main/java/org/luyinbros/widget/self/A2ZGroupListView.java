package org.luyinbros.widget.self;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class A2ZGroupListView extends FrameLayout {
    private RecyclerView mRecyclerView;
    private A2ZSideBar mSideBar;
    private Adapter mAdapter;

    public A2ZGroupListView(@NonNull Context context) {
        this(context, null);
    }

    public A2ZGroupListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public A2ZGroupListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        addView(mRecyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        {
            mSideBar = new A2ZSideBar(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER | Gravity.END;
            layoutParams.rightMargin = 20;
            mSideBar.setPadding(20, 0, 20, 0);
            mSideBar.setBackgroundColor(0xFF566EFF);
            mSideBar.setLayoutParams(layoutParams);
            mSideBar.setOnLetterSelectedListener(new A2ZSideBar.OnLetterSelectedListener() {
                @Override
                public void onLetterSelected(int position) {
                    if (position > -1) {
                        if (mAdapter != null) {
                            mRecyclerView.scrollToPosition(mAdapter.innerAdapter.getGroupRealPosition(position));
                        }
                    } else {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            });
            addView(mSideBar);
        }


    }

    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            mAdapter = null;
            mRecyclerView.setAdapter(null);
        } else {
            mAdapter = adapter;
            mRecyclerView.setAdapter(new InnerAdapter(mAdapter));
            notifyDataSetChanged();
        }
    }

    public final void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < mAdapter.getGroupItemCount(); i++) {
                list.add(mAdapter.getGroupMarkString(i));
            }
            mSideBar.setup(list);
        }
    }

    private static class InnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Adapter mAdapter;
        private static final int TYPE_HEADER = 1 << 16;
        private static final int TYPE_GROUP = 2 << 16;
        private static final int TYPE_GROUP_CHILD = 3 << 16;

        InnerAdapter(Adapter adapter) {
            this.mAdapter = adapter;
            this.mAdapter.innerAdapter = this;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            int realType = getHeaderViewType(viewType);
            if (realType != -1) {
                return mAdapter.onCreateHeaderHolder(viewGroup, realType);
            }
            realType = getGroupViewType(viewType);
            if (realType != -1) {
                return mAdapter.onCreateGroupHolder(viewGroup, realType);
            }
            realType = getGroupChildViewType(viewType);
            if (realType != -1) {
                return mAdapter.onCreateGroupChildHolder(viewGroup, realType);
            }
            throw new IllegalStateException("no match holder");
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            int totalCount = mAdapter.getHeaderItemCount();
            if (totalCount != 0 && position < totalCount) {
                mAdapter.onBindHeaderHolder(viewHolder, position);
            } else {
                int groupTotalCount = mAdapter.getGroupItemCount();
                if (groupTotalCount > 0) {
                    for (int i = 0; i < groupTotalCount; i++) {
                        totalCount += 1;
                        if (position < totalCount) {
                            mAdapter.onBindGroupHolder(viewHolder, i);
                            break;
                        }
                        int groupChildCount = mAdapter.getGroupChildItemCount(i);
                        if (groupChildCount > 0) {
                            totalCount += groupChildCount;
                            if (position < totalCount) {
                                mAdapter.onBindGroupChildHolder(viewHolder, i, position - (totalCount - groupChildCount));
                                break;
                            }
                        }
                    }
                }

            }
        }

        @Override
        public int getItemViewType(int position) {
            int totalCount = mAdapter.getHeaderItemCount();
            if (totalCount != 0 & position < totalCount) {
                return mAdapter.getHeaderViewType(position) | TYPE_HEADER;
            }
            int groupTotalCount = mAdapter.getGroupItemCount();
            if (groupTotalCount > 0) {
                for (int i = 0; i < groupTotalCount; i++) {
                    totalCount += 1;
                    if (position < totalCount) {
                        return mAdapter.getGroupViewType(i) | TYPE_GROUP;
                    }
                    int groupChildCount = mAdapter.getGroupChildItemCount(i);
                    if (groupChildCount > 0) {
                        totalCount += groupChildCount;
                        if (position < totalCount) {
                            return mAdapter.getGroupChildViewType(i, position - (totalCount - groupChildCount)) | TYPE_GROUP_CHILD;
                        }
                    }
                }
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            int totalCount = mAdapter.getHeaderItemCount();
            int groupTotalCount = mAdapter.getGroupItemCount();
            totalCount += groupTotalCount;
            if (groupTotalCount > 0) {
                for (int i = 0; i < groupTotalCount; i++) {
                    totalCount += mAdapter.getGroupChildItemCount(i);
                }
            }
            return totalCount;
        }

        private int getHeaderViewType(int sourceViewType) {

            if ((sourceViewType & 0xFFFF0000) == TYPE_HEADER) {
                return sourceViewType & 0x0000FFFF;
            }
            return -1;
        }

        private int getGroupViewType(int sourceViewType) {
            if ((sourceViewType & 0xFFFF0000) == TYPE_GROUP) {
                return sourceViewType & 0x0000FFFF;
            }
            return -1;
        }

        private int getGroupChildViewType(int sourceViewType) {
            if ((sourceViewType & 0xFFFF0000) == TYPE_GROUP_CHILD) {
                return sourceViewType & 0x0000FFFF;
            }
            return -1;
        }

        private int getHeaderPosition(int position) {
            int totalCount = mAdapter.getHeaderItemCount();
            if (totalCount != 0 & position < totalCount) {
                return position;
            }
            return -1;
        }

        private int getGroupPosition(int position) {
            int totalCount = mAdapter.getHeaderItemCount();
            if (totalCount != 0 && position < totalCount) {
                return -1;
            } else {
                int groupTotalCount = mAdapter.getGroupItemCount();
                if (groupTotalCount > 0) {
                    for (int i = 0; i < groupTotalCount; i++) {
                        totalCount += 1;
                        if (position < totalCount) {
                            return i;
                        }
                        totalCount += mAdapter.getGroupChildItemCount(i);
                    }
                }

                return -1;
            }
        }

        private int getGroupChildPosition(int position) {
            int totalCount = mAdapter.getHeaderItemCount();
            if (totalCount != 0 && position < totalCount) {
                return -1;
            } else {
                int groupTotalCount = mAdapter.getGroupItemCount();
                if (groupTotalCount > 0) {
                    for (int i = 0; i < groupTotalCount; i++) {
                        totalCount += 1;
                        if (position < totalCount) {
                            return i;
                        }
                        int groupChildCount = mAdapter.getGroupChildItemCount(i);
                        if (groupChildCount > 0) {
                            totalCount += groupChildCount;
                            if (position < totalCount) {
                                return position - (totalCount - groupChildCount);
                            }
                        }
                    }
                }

                return -1;
            }
        }

        private int getGroupChildGroupPosition(int position) {
            int totalCount = mAdapter.getHeaderItemCount();
            if (totalCount != 0 && position < totalCount) {
                return -1;
            } else {
                int groupTotalCount = mAdapter.getGroupItemCount();
                if (groupTotalCount > 0) {
                    for (int i = 0; i < groupTotalCount; i++) {
                        totalCount += 1;
                        if (position < totalCount) {
                            return i;
                        }
                        int groupChildCount = mAdapter.getGroupChildItemCount(i);
                        if (groupChildCount > 0) {
                            totalCount += groupChildCount;
                            if (position < totalCount) {
                                return i;
                            }
                        }
                    }
                }

                return -1;
            }
        }

        //TODO 修复这个问题
        private int getGroupRealPosition(int groupPosition) {
            int groupTotalCount = mAdapter.getGroupItemCount();
            if (groupPosition >= groupTotalCount) {
                return -1;
            }
            int totalCount = mAdapter.getHeaderItemCount();
            for (int i = 0; i < groupPosition; i++) {
                totalCount++;
                totalCount += mAdapter.getGroupChildItemCount(i);
            }
            return totalCount ;
        }
    }

    public abstract static class Adapter<HeaderHolder extends RecyclerView.ViewHolder,
            GroupHolder extends RecyclerView.ViewHolder,
            GroupChildHolder extends RecyclerView.ViewHolder> {
        @NonNull
        private InnerAdapter innerAdapter;

        @NonNull
        public abstract HeaderHolder onCreateHeaderHolder(ViewGroup parent, int viewType);

        public abstract void onBindHeaderHolder(@NonNull HeaderHolder holder, int headerIndex);

        @IntRange(from = 0)
        public int getHeaderViewType(int headerIndex) {
            return 0;
        }

        public abstract int getHeaderItemCount();

        @NonNull
        public abstract GroupHolder onCreateGroupHolder(ViewGroup parent, int viewType);

        public abstract void onBindGroupHolder(@NonNull GroupHolder holder, int groupIndex);

        @IntRange(from = 0)
        public int getGroupViewType(int groupIndex) {
            return 0;
        }

        public abstract int getGroupItemCount();

        @NonNull
        public abstract GroupChildHolder onCreateGroupChildHolder(ViewGroup parent, int viewType);

        public abstract void onBindGroupChildHolder(@NonNull GroupChildHolder holder, int groupIndex, int childIndex);

        public abstract String getGroupMarkString(int groupIndex);

        @IntRange(from = 0)
        public int getGroupChildViewType(int groupIndex, int childIndex) {
            return 0;
        }

        public abstract int getGroupChildItemCount(int groupIndex);

        public final int getHeaderPosition(HeaderHolder holder) {
            return innerAdapter.getHeaderPosition(holder.getAdapterPosition());
        }

        public final int getGroupPosition(GroupHolder holder) {
            return innerAdapter.getGroupPosition(holder.getAdapterPosition());
        }

        public final int getGroupChildPosition(GroupChildHolder holder) {
            return innerAdapter.getGroupChildPosition(holder.getAdapterPosition());
        }

        public final int getGroupChildGroupPosition(GroupChildHolder holder) {
            return innerAdapter.getGroupChildGroupPosition(holder.getAdapterPosition());
        }

        public final void notifyDataSetChanged() {
            innerAdapter.notifyDataSetChanged();
        }

    }
}
