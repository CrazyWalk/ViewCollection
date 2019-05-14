package org.luyinbros.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.luyinbros.widget.PagerAdapter;
import org.luyinbros.widget.R;
import org.luyinbros.widget.self.MusicbibleTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MusicbibleTabLayoutActivity extends AppCompatActivity {
    private MusicbibleTabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicbible_tab_layout);
        mTabLayout = findViewById(R.id.mTabLayout);
        mViewPager = findViewById(R.id.mViewPager);

        final List<CharSequence> titleArray = new ArrayList<>(3);
        titleArray.add("通知");
        titleArray.add("评论");
        titleArray.add("收藏");
        mTabLayout.setTitleArray(titleArray);
        mTabLayout.setTipText(0, "99+");
        mTabLayout.setTipText(2, "2");
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new Adater());
        mTabLayout.setupViewPager(mViewPager);

    }

    private static class Adater extends PagerAdapter<Holder> {

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(new ImageView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (position == 0) {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.RED));
            } else if (position == 1) {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.GREEN));
            } else if (position == 2) {
                holder.imageView.setImageDrawable(new ColorDrawable(Color.GRAY));
            }

        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    static class Holder extends PagerAdapter.ViewHolder {
        ImageView imageView;

        public Holder(@NonNull ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
}
