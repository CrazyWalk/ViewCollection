package org.luyinbros.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.Toasts;
import org.luyinbros.widget.self.A2ZGroupListView;
import org.luyinbros.widget.self.A2ZSideBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A2ZActivity extends AppCompatActivity {
    private A2ZGroupListView a2ZGroupListView;
    private A2ZSideBar mSideBar;
    private A2ZSideBar.OnLetterSelectedListener onLetterSelectedListener;
    private static final String TAG = "A2ZActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_a_2_z_view);
//        mSideBar = findViewById(R.id.mSideBar);
//        mSideBar.setOnLetterSelectedListener(new A2ZSideBar.OnLetterSelectedListener() {
//            @Override
//            public void onLetterSelected(int position) {
//                if (position != -1) {
//                    Log.d(TAG, "onLetterSelected: " + mSideBar.mLetters.get(position));
//                }
//
//            }
//        });
        a2ZGroupListView = findViewById(R.id.mGroupListView);
        a2ZGroupListView.setAdapter(new InnerAdapter());
    }

    private static class InnerAdapter extends A2ZGroupListView.Adapter<HeaderHolder, GroupHolder, ChildHolder> {
        private List<String> mHeaderDataList;
        private List<String> mGroupDataList;
        private List<List<String>> mGroupChildDataList;

        public InnerAdapter() {
            mHeaderDataList = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                mHeaderDataList.add("header:" + i);
            }
            mGroupDataList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                mGroupDataList.add("group:" + i);
            }

            mGroupChildDataList = new ArrayList<>();
            mGroupChildDataList.add(Arrays.asList("group0:child 0", "group0:child 1", "group0:child 2"));
            mGroupChildDataList.add(new ArrayList<String>());
            mGroupChildDataList.add(new ArrayList<String>());
            mGroupChildDataList.add(Arrays.asList("group3:child 0", "group3:child 1"));
            mGroupChildDataList.add(new ArrayList<String>());
        }

        @NonNull
        @Override
        public HeaderHolder onCreateHeaderHolder(ViewGroup parent, int viewType) {
            final HeaderHolder headerHolder = new HeaderHolder(new TextView(parent.getContext()));
            headerHolder.mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            headerHolder.mTextView.setBackgroundColor(0xFFFFBC42);
            headerHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toasts.show(v.getContext(), "header click . index:" + getHeaderPosition(headerHolder));
                }
            });
            return headerHolder;
        }

        @Override
        public void onBindHeaderHolder(@NonNull HeaderHolder holder, int headerIndex) {
            holder.mTextView.setText(mHeaderDataList.get(headerIndex));
        }

        @Override
        public int getHeaderItemCount() {
            return mHeaderDataList.size();
        }

        @NonNull
        @Override
        public GroupHolder onCreateGroupHolder(ViewGroup parent, int viewType) {
            final GroupHolder groupHolder = new GroupHolder(new TextView(parent.getContext()));
            groupHolder.mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            groupHolder.mTextView.setBackgroundColor(0xFFFF3C27);
            groupHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toasts.show(v.getContext(), "group click . index:" + getGroupPosition(groupHolder));
                }
            });
            return groupHolder;
        }

        @Override
        public void onBindGroupHolder(@NonNull GroupHolder holder, int groupIndex) {
            holder.mTextView.setText(mGroupDataList.get(groupIndex));
        }

        @Override
        public int getGroupItemCount() {
            return mGroupDataList.size();
        }

        @NonNull
        @Override
        public ChildHolder onCreateGroupChildHolder(ViewGroup parent, int viewType) {
            final ChildHolder childHolder = new ChildHolder(new TextView(parent.getContext()));
            childHolder.mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            childHolder.mTextView.setBackgroundColor(0xFFFEFF4D);
            childHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toasts.show(v.getContext(), "group click . group index:" + getGroupChildGroupPosition(childHolder) + ". child index: " + getGroupChildPosition(childHolder));
                }
            });
            return childHolder;
        }

        @Override
        public void onBindGroupChildHolder(@NonNull ChildHolder holder, int groupIndex, int childIndex) {
            holder.mTextView.setText(mGroupChildDataList.get(groupIndex).get(childIndex));
        }

        @Override
        public int getGroupChildItemCount(int groupIndex) {
            return mGroupChildDataList.get(groupIndex).size();
        }

        @Override
        public String getGroupMarkString(int groupIndex) {
            return groupIndex + "";
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public HeaderHolder(@NonNull TextView textView) {
            super(textView);
            this.mTextView = textView;
        }
    }

    static class GroupHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public GroupHolder(@NonNull TextView textView) {
            super(textView);
            this.mTextView = textView;
        }
    }

    static class ChildHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public ChildHolder(@NonNull TextView textView) {
            super(textView);
            this.mTextView = textView;
        }
    }
}
