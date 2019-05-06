package org.luyinbros.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.self.AvatarSetView;
import org.luyinbros.widget.self.HeartTextView;
import org.luyinbros.widget.self.PraiseTextView;

import java.util.ArrayList;
import java.util.List;

public class SmallWidgetActivity extends AppCompatActivity {
    private HeartTextView heartView;
    private PraiseTextView praiseTextView;
    private AvatarSetView avatarSetView;
    private AvatarSetAdapter avatarAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_widget);
        heartView = findViewById(R.id.heartCountView);
        praiseTextView = findViewById(R.id.praiseTextView);
        avatarSetView = findViewById(R.id.avatarSetView);
        heartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartView.setSelected(!v.isSelected());
            }
        });
        praiseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseTextView.setSelected(!v.isSelected());
            }
        });
        findViewById(R.id.avatarSetViewRemoveButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (avatarAdapter.mList.size() != 0) {
//                            int removePosition = avatarAdapter.mList.size() / 2;
//                            avatarAdapter.notifyItemRemoved(removePosition);
//                            avatarAdapter.mList.remove(removePosition);
//                        }

                    }
                });
        findViewById(R.id.avatarSetViewInsertButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        int insertPosition = avatarAdapter.mList.size() / 2;
//                        avatarAdapter.mList.add(insertPosition, avatarAdapter.newRandomDrawable());
//                        avatarAdapter.notifyItemInserted(insertPosition);
                    }
                });

        avatarAdapter = new AvatarSetAdapter();
        avatarSetView.setAdapter(avatarAdapter);


    }

    private static class AvatarSetAdapter extends AvatarSetView.Adapter {
        List<Drawable> mList;
        private int[] colors = {Color.RED, Color.BLACK, Color.GREEN,Color.RED};

        {
            mList = new ArrayList<>();
            mList.add(new ColorDrawable(colors[0]));
            mList.add(new ColorDrawable(colors[1]));
            mList.add(new ColorDrawable(colors[2]));
            mList.add(new ColorDrawable(colors[3]));
            //mList.add(newRandomDrawable());
        }

        private Drawable newRandomDrawable() {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.argb(
                    255,
                    (int) (Math.random() * 266),
                    (int) (Math.random() * 266),
                    (int) (Math.random() * 266)));
            gradientDrawable.setShape(GradientDrawable.OVAL);
            return gradientDrawable;
        }

        @Override
        public void onBindAvatarItemView(ImageView imageView, int position) {
            imageView.setImageDrawable(mList.get(position));
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public int getMaxVisibleCount() {
            return 3;
        }
    }

}
