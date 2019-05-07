package org.luyinbros.widget.list;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class AbstractSimpleListController<VH extends AbstractSimpleListController.ViewHolder> implements ListController<VH> {
    private List<VH> mViewHolderList = new ArrayList<>();
    private ViewGroup mParent;

    public AbstractSimpleListController(@NonNull ViewGroup parent) {
        this.mParent = parent;
        mParent.removeAllViews();
    }

    @NonNull
    public abstract VH onCreateHolder(ViewGroup container, int viewType);

    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    @Override
    public abstract int getItemCount();


    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public final void notifyDataSetInvalidated() {
        mViewHolderList = new ArrayList<>(getItemCount());
        mParent.removeAllViews();
        VH viewHolder;
        for (int i = 0; i < getItemCount(); i++) {
            viewHolder = _onCreateHolder(mParent, getItemViewType(i));
            //必须对调，要不然无法手动改layoutParams
            mParent.addView(viewHolder.itemView);
            onBindViewHolder(viewHolder, i);
            mViewHolderList.add(viewHolder);
        }
    }

    @Override
    public final void notifyDataSetChanged() {
        checkViewHolder();
        List<VH> newHolderList = new ArrayList<>(getItemCount());
        mParent.removeAllViews();
        VH viewHolder;
        for (int i = 0; i < getItemCount(); i++) {
            if (i < mViewHolderList.size()) {
                viewHolder = mViewHolderList.get(i);
                if (isMatchHolder(viewHolder, i)) {
                    onBindViewHolder(viewHolder, i);
                } else {
                    viewHolder = _onCreateHolder(mParent, getItemViewType(i));
                    onBindViewHolder(viewHolder, i);
                    mParent.addView(viewHolder.itemView);
                    newHolderList.add(viewHolder);
                }
                newHolderList.add(viewHolder);
            } else {
                viewHolder = _onCreateHolder(mParent, getItemViewType(i));
                onBindViewHolder(viewHolder, i);
                mParent.addView(viewHolder.itemView);
                newHolderList.add(viewHolder);
            }
        }
        mViewHolderList = newHolderList;
    }

    @Override
    public final void notifyItemChanged(int position) {
        if (position >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else {
            checkViewHolder();
            int cacheHolderSize = mViewHolderList.size();
            if (position < cacheHolderSize) {
                VH cacheHolder = mViewHolderList.get(position);
                if (isMatchHolder(cacheHolder, position)) {
                    onBindViewHolder(cacheHolder, position);
                } else {
                    notifyItemRemoved(position);
                    notifyItemInserted(position);
                }
            } else if (position == cacheHolderSize) {
                notifyItemInserted(position);
            } else {
                throw new IndexOutOfBoundsException("");
            }

        }
    }

    @Override
    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        if (positionStart >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else if (positionStart + itemCount >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else {
            checkViewHolder();
            int cacheHolderSize = mViewHolderList.size();
            for (int i = positionStart; i < positionStart + itemCount; i++) {
                VH cacheHolder = mViewHolderList.get(i);
                if (i < cacheHolderSize) {
                    if (isMatchHolder(cacheHolder, i)) {
                        onBindViewHolder(cacheHolder, i);
                    } else {
                        notifyItemRemoved(i);
                        notifyItemInserted(i);
                    }
                } else {
                    notifyItemInserted(i);
                }

            }

        }
    }

    @Override
    public final void notifyItemInserted(int position) {
        if (position >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else {
            checkViewHolder();
            VH viewHolder = _onCreateHolder(mParent, getItemViewType(position));
            onBindViewHolder(viewHolder, position);
            mParent.addView(viewHolder.itemView, position);
            mViewHolderList.add(position, viewHolder);
        }
    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (positionStart >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else if (positionStart + itemCount >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else {
            checkViewHolder();
            int cacheHolderSize = mViewHolderList.size();
            if (positionStart >= cacheHolderSize) {
                for (int i = positionStart; i < positionStart + itemCount; i++) {
                    notifyItemInserted(i);
                }
            } else {
                throw new IndexOutOfBoundsException("");
            }
        }
    }

    @Override
    public final void notifyItemRemoved(int position) {
        if (position >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else {
            checkViewHolder();
            mViewHolderList.remove(position);
            mParent.removeViewAt(position);
        }
    }

    @Override
    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        if (positionStart >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else if (positionStart + itemCount >= getItemCount()) {
            throw new IndexOutOfBoundsException("");
        } else {
            checkViewHolder();
            int cacheHolderSize = mViewHolderList.size();
            if (positionStart >= cacheHolderSize) {
                for (int i = positionStart + itemCount; i > positionStart - 1; i++) {
                    notifyItemRemoved(i);
                }
            } else {
                throw new IndexOutOfBoundsException("");
            }
        }
    }

    @Override
    public int getHolderPosition(VH holder) {
        checkViewHolder();
        return mViewHolderList.indexOf(holder);
    }

    private void checkViewHolder() {
        int cacheHolderSize = mViewHolderList.size();
        int parentChildSize = mParent.getChildCount();
        if (cacheHolderSize == parentChildSize) {
            ViewHolder holder;
            View child;
            for (int i = 0; i < parentChildSize; i++) {
                holder = mViewHolderList.get(i);
                child = mParent.getChildAt(i);
                if (holder.mItemViewType != getItemViewType(i) || holder.itemView != child) {
                    notifyDataSetInvalidated();
                    break;
                }
            }
        } else {
            notifyDataSetInvalidated();
        }
    }

    private boolean isMatchHolder(ViewHolder holder, int position) {
        ViewHolder viewHolder = mViewHolderList.get(position);
        return getItemViewType(position) == viewHolder.mItemViewType && viewHolder.itemView == mParent.getChildAt(position);
    }

    private VH _onCreateHolder(ViewGroup container, int viewType) {
        VH vh = onCreateHolder(container, viewType);
        vh.mItemViewType = viewType;
        vh.listController = this;
        return vh;
    }


}
