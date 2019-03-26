package org.luyinbros.widget.list;

public interface ListController {

    int getItemCount();


    void notifyDataSetInvalidated();

    void notifyDataSetChanged();

    void notifyItemChanged(int position);

    void notifyItemRangeChanged(int positionStart, int itemCount);

    void notifyItemInserted(int position);

    void notifyItemRangeInserted(int positionStart, int itemCount);

    void notifyItemRemoved(int position);

    void notifyItemRangeRemoved(int positionStart, int itemCount);
}
