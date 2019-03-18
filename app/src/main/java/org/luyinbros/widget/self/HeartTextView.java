package org.luyinbros.widget.self;


import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import org.luyinbros.widget.R;

public class HeartTextView extends ViewGroup {
    private HeartViewDelegate mDelegate;
    private TextView mCountText;

    public HeartTextView(Context context) {
        this(context, null);
    }

    public HeartTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
        mDelegate = new HeartViewDelegate(this);
        mDelegate.setImageResource(R.mipmap.ic_heart_unselected);
        mDelegate.setRippleImageResource(R.mipmap.ic_heart_ripple);
        mCountText = new AppCompatTextView(context);
        mCountText.setText("1");
        addView(mCountText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mDelegate.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mCountText, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                mDelegate.getMeasuredWidth() + mCountText.getMeasuredWidth(),
                Math.max(mDelegate.getMeasuredHeight(), mCountText.getMeasuredHeight())
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = (getMeasuredHeight() - mDelegate.getMeasuredHeight()) / 2;
        mDelegate.layout(left,
                top,
                left + mDelegate.getMeasuredWidth(),
                top + mDelegate.getMeasuredHeight());

        left += mDelegate.getMeasuredWidth();
        {
            int iTop=(getMeasuredHeight() - mCountText.getMeasuredHeight()) / 2;
            mCountText.layout(left,
                    iTop,
                    left + mDelegate.getMeasuredWidth(),
                    iTop + mDelegate.getMeasuredHeight());
        }
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mDelegate.setImageResource(R.mipmap.ic_heart_selected, true);
        } else {
            mDelegate.setImageResource(R.mipmap.ic_heart_unselected);
        }
    }
}
