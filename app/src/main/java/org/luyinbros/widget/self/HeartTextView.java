package org.luyinbros.widget.self;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.luyinbros.widget.R;

public class HeartTextView extends ViewGroup {
    private ImageView heartImageView;
    private TextView countTextView;

    public HeartTextView(@NonNull Context context) {
        this(context, null);
    }

    public HeartTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        heartImageView = new ImageView(getContext());
        heartImageView.setImageResource(R.mipmap.ic_heart_unselected);
        addView(heartImageView);

        setCountText("1");
    }

    public void setCountText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (countTextView == null) {
                countTextView = new TextView(getContext());
                countTextView.setTextColor(0x74191D26);
                countTextView.setTextSize(12);
            }
            if (!shouldLayout(countTextView)) {
                addView(countTextView);
            }
        } else {
            if (countTextView != null) {
                removeView(countTextView);
            }
        }
        if (countTextView != null) {
            countTextView.setText(text);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int totalHeight = 0;
        measureChild(heartImageView, widthMeasureSpec, heightMeasureSpec);
        totalWidth += heartImageView.getMeasuredWidth();
        totalHeight = Math.max(heartImageView.getMeasuredHeight(), totalHeight);
        if (shouldLayout(countTextView)) {
            measureChild(countTextView, widthMeasureSpec, heightMeasureSpec);
            totalWidth += countTextView.getMeasuredWidth();
            totalHeight = Math.max(countTextView.getMeasuredHeight(), totalHeight);
        }
        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        heartImageView.layout(
                0,
                0,
                heartImageView.getMeasuredWidth(),
                heartImageView.getMeasuredHeight()
        );

        if (shouldLayout(countTextView)) {
            int left = heartImageView.getMeasuredWidth();
            int top = heartImageView.getMeasuredHeight() / 2 - countTextView.getMeasuredHeight() / 2;
            countTextView.layout(
                    left,
                    top,
                    left + countTextView.getMeasuredWidth(),
                    top + countTextView.getMeasuredHeight()
            );
        }
    }

    private boolean shouldLayout(View view) {
        return view != null && view.getParent() == this && view.getVisibility() != GONE;
    }
}
