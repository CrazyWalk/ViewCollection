package org.luyinbros.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import org.luyinbros.utils.Dimens;
import org.luyinbros.widget.PagerAdapter;
import org.luyinbros.widget.R;
import org.luyinbros.widget.recyclerview.LeftSnapHelper;
import org.luyinbros.widget.recyclerview.ScrollSpeedLinearLayoutManger;


public class ViewPagerActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ViewPager mViewPager2;
    private RecyclerView mViewPager3;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mViewPager = findViewById(R.id.mViewPager);
        mViewPager2 = findViewById(R.id.mViewPager2);
        mViewPager.setClipChildren(false);
        //配合padding和 pageAdapter .getPageWidth达到想要的效果
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(20);
        mViewPager.setAdapter(new Adapter());

        //需要用帧布局包裹
        mViewPager2.setClipChildren(false);
        mViewPager2.setPageMargin(20);
        mViewPager2.setAdapter(new Adapter2());

        mViewPager3 = findViewById(R.id.mViewPager3);
//        ScrollSpeedLinearLayoutManger scrollSpeedLinearLayoutManger = new ScrollSpeedLinearLayoutManger(this, LinearLayoutManager.HORIZONTAL, false);
//        scrollSpeedLinearLayoutManger.setSpeedSlow();
//        mViewPager3.setLayoutManager(scrollSpeedLinearLayoutManger);
//        LinearSnapHelper mLinearSnapHelper = new LeftSnapHelper();
//        mLinearSnapHelper.attachToRecyclerView(mViewPager3);

        mViewPager3.setAdapter(new RecyclerView.Adapter<RecyclerViewHolder>() {
            @NonNull
            @Override
            public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                RecyclerViewHolder holder = new RecyclerViewHolder(new ImageView(viewGroup.getContext()));
                ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(Dimens.px(viewGroup.getContext(), 130), Dimens.px(viewGroup.getContext(), 169));
                marginLayoutParams.leftMargin=Dimens.px(viewGroup.getContext(),20);
                holder.imageView.setLayoutParams(marginLayoutParams);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                if (position % 2 == 0) {
                    holder.imageView.setImageDrawable(new ColorDrawable(Color.RED));
                } else {
                    holder.imageView.setImageDrawable(new ColorDrawable(Color.GREEN));
                }
            }

            @Override
            public int getItemCount() {
                return 10;
            }
        });
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public RecyclerViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
            //设置宽高在viewPager无效
        }
    }

    private static class Adapter extends PagerAdapter<ViewHolder> {

        @Override
        public ViewPagerActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewPagerActivity.ViewHolder(new ImageView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewPagerActivity.ViewHolder holder, int position) {
            if (position % 2 == 0) {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.RED));
            } else {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.GREEN));
            }
        }

        @Override
        public float getPageWidth(int position) {
            return 0.5f;
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

    static class ViewHolder extends PagerAdapter.ViewHolder {
        private ImageView imageView;

        public ViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
            //设置宽高在viewPager无效
        }
    }

    private static class Adapter2 extends PagerAdapter<ViewHolder> {

        @Override
        public ViewPagerActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewPagerActivity.ViewHolder(new ImageView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewPagerActivity.ViewHolder holder, int position) {
            if (position % 2 == 0) {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.RED));
            } else {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.GREEN));
            }
        }

        @Override
        public float getPageWidth(int position) {
            return 0.8f;
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

    private int px(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
