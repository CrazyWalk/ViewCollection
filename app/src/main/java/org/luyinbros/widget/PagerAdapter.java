package org.luyinbros.widget;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public abstract class PagerAdapter<VH extends PagerAdapter.ViewHolder> extends android.support.v4.view.PagerAdapter {
    private int mChildCount = 0;

    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    public abstract int getCount();

    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        int viewType = getItemViewType(position);
        VH holder = onCreateViewHolder(container, viewType);
        onBindViewHolder(holder, position);
        container.addView(holder.itemView);
        return holder;
    }

    @Override
    public final void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
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

    @Override
    public final int getItemPosition(@NonNull Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }


    public static class ViewHolder {
        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }
    }


}
