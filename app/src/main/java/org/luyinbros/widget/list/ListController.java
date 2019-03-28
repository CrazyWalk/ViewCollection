package org.luyinbros.widget.list;

import android.support.annotation.NonNull;
import android.view.View;

public interface ListController<VH extends ListController.ViewHolder> {

    int getItemCount();

    int getHolderPosition(VH holder);

    void notifyDataSetInvalidated();

    void notifyDataSetChanged();

    void notifyItemChanged(int position);

    void notifyItemRangeChanged(int positionStart, int itemCount);

    void notifyItemInserted(int position);

    void notifyItemRangeInserted(int positionStart, int itemCount);

    void notifyItemRemoved(int position);

    void notifyItemRangeRemoved(int positionStart, int itemCount);

    class ViewHolder {
        public View itemView;
        int mItemViewType = -1;
        ListController listController;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        public int getItemViewType() {
            return mItemViewType;
        }

        public int getPosition() {
            if (listController == null) {
                return -1;
            }
            return listController.getHolderPosition(this);
        }
    }
}
