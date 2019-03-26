package org.luyinbros.widget.list;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleListAdapter<VH extends SimpleListAdapter.ViewHolder> {
    private List<ViewHolder> mViewHolderList = new ArrayList<>();

    public abstract VH onCreateHolder(ViewGroup container, int viewType);

    public abstract void onBindViewHolder(VH holder, int position);

    public abstract int getItemCount();

    public int getItemViewType(int position) {
        return 0;
    }

    public final void notifyDataSetChanged() {

    }

    public final void notifyItemChanged(int position) {

    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {

    }

    public final void notifyItemInserted(int position) {

    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount) {

    }

    public final void notifyItemRemoved(int position) {

    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {

    }

    public static class ViewHolder {
        public  View itemView;
        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
