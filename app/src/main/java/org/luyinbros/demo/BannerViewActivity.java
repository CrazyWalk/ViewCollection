package org.luyinbros.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.self.BannerView;
import org.luyinbros.widget.self.DefaultBannerIndicator;

public class BannerViewActivity extends AppCompatActivity {
    private BannerView bannerView;
    private Adapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_view);
        bannerView = findViewById(R.id.bannerView);
        mAdapter = new Adapter();
        bannerView.setAdapter(mAdapter);
        bannerView.setAutoScrollEnable(true);
        bannerView.setInfinite(true);
        bannerView.setIndicator(new DefaultBannerIndicator(this));

        findViewById(R.id.color1Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.colors = new int[]{0xFFFF7761, 0xFFD3FF45, 0xFF33BAFF};
                mAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.color2Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.colors = new int[]{0xFFD3FF45, 0xFFD3FFF1, 0xFF33BAFF, 0xFF4317FF};
                mAdapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.color3Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.colors = new int[]{Color.RED};
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private static class Adapter extends BannerView.Adapter<ViewHolder> {
        private int[] colors;

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.imageView.setBackgroundColor(colors[position]);
        }

        @Override
        public int getItemCount() {
            return colors != null ? colors.length : 0;
        }
    }

    private static class ViewHolder extends BannerView.ViewHolder {
        private ImageView imageView;

        ViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}
