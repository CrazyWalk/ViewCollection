package org.luyinbros.widget.self;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class A2ZGroupListView extends RecyclerView {

    public A2ZGroupListView(@NonNull Context context) {
        super(context);
    }

    public A2ZGroupListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public A2ZGroupListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public abstract static class Adapter<HeaderHolder, GroupHolder, GroupChildHolder> {

        @NonNull
        public abstract HeaderHolder onCreateHeaderHolder(ViewGroup parent, int viewType);

        public abstract void onBindHeaderHolder(HeaderHolder holder, int headerIndex);

        public int getHeaderViewType(int headerIndex) {
            return 0;
        }

        public abstract int getHeaderItemCount();

        public int getGroupViewType(int groupIndex) {
            return 0;
        }

        public abstract int getGroupItemCount();

        public int getGroupChildViewType(int groupIndex, int childIndex) {
            return 0;
        }

        public abstract int getGroupChildItemCount(int groupIndex);


    }
}
