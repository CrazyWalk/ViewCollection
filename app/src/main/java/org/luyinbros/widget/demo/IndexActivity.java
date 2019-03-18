package org.luyinbros.widget.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class IndexActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new Adapter());
        setContentView(mRecyclerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private IndexItem[] mArray = new IndexItem[]{
                new IndexItem("广告轮播图", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(v.getContext(), BannerViewActivity.class);
                    }
                }),
                new IndexItem("小控件", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(v.getContext(), SmallWidgetActivity.class);
                    }
                }),
                new IndexItem("状态视图", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(v.getContext(), StatusViewActivity.class);
                    }
                })
        };

        private void startActivity(Context context, Class c) {
            Intent intent = new Intent(context, c);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView textView = new TextView(viewGroup.getContext());
            textView.setTextSize(23);
            textView.setPadding(20, 20, 20, 20);
            final ViewHolder holder = new ViewHolder(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View.OnClickListener onClickListener = mArray[holder.getAdapterPosition()].mOnClickListener;
                    if (onClickListener != null) {
                        onClickListener.onClick(v);
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.mTextView.setText(mArray[i].name);
        }

        @Override
        public int getItemCount() {
            return mArray.length;
        }
    }

    private static class IndexItem {
        private CharSequence name;
        private View.OnClickListener mOnClickListener;

        public IndexItem(CharSequence name, View.OnClickListener onClickListener) {
            this.name = name;
            this.mOnClickListener = onClickListener;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public ViewHolder(@NonNull TextView itemView) {
            super(itemView);
            mTextView = itemView;
        }
    }

}
