package org.luyinbros.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.luyinbros.widget.R;
import org.luyinbros.widget.UIPopupWindow;
import org.luyinbros.widget.UIWindow;

public class PopupWindowActivity extends AppCompatActivity {
    private static final String TAG = "PopupWindowTAG";
    //  private UI ui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new UIFragment())
                .commitNow();
//        ui = new UI(this);
//        setContentView(ui);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "Activity onAttachedToWindow: ");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "Activity onDetachedFromWindow: ");
    }

    public static class UIFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return new UI(inflater.getContext());
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, new UIFragment())
                            .commitNow();
                }
            });
        }
    }

    private static class UI extends ConstraintLayout {
        private TextView showPopupWindowButton;
        private UIPopupWindow simpleTipView;

        public UI(final Context context) {
            super(context);
            inflate(context, R.layout.activity_popup_window, this);
            showPopupWindowButton = findViewById(R.id.showPopupWindowButton);
            showPopupWindowButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleTipView.showAtAnchorView(showPopupWindowButton, UIPopupWindow.VerticalGravity.BELOW, UIPopupWindow.HorizontalGravity.ALIGN_LEFT);
                }
            });
            initSimpleTipView();
        }

        /**
         * Activity:onCreate
         * Activity:onStart
         * Activity:onResume
         * Activity:onAttachedToWindow
         * View:onAttachedToWindow
         * View:onWindowVisibilityChanged
         */
        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            //  UIWindow.darkImmersive(getContext());
            Log.d(TAG, "View onAttachedToWindow: ");
        }

        /**
         *
         */
        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Log.d(TAG, "View onDetachedFromWindow: ");
            simpleTipView.dismiss(false);
        }


        @Override
        protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
            super.onVisibilityChanged(changedView, visibility);
        }

        /**
         * fragment 可能需要用到
         *
         * @param visibility
         */
        @Override
        public void setVisibility(int visibility) {
            super.setVisibility(visibility);
        }

        /**
         * 重写此方法能准确的知道view的状态
         * Activity onPause
         * Activity onWindowVisibilityChanged
         * Activity onStop
         * View onWindowVisibilityChanged
         * <p>
         * Activity onStart
         * Activity onResume
         * View onWindowVisibilityChanged
         */
        @Override
        protected void onWindowVisibilityChanged(int visibility) {
            super.onWindowVisibilityChanged(visibility);
            Log.d(TAG, "onWindowVisibilityChanged: " + visibility);
        }

        @Override
        public void onWindowSystemUiVisibilityChanged(int visible) {
            super.onWindowSystemUiVisibilityChanged(visible);
        }

        private void initSimpleTipView() {
            simpleTipView = new UIPopupWindow(getContext());
            TextView textView = new TextView(getContext());
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(0xFFFF9F06);
            textView.setText("提示");
            textView.setGravity(Gravity.CENTER);
            simpleTipView.setContentView(textView);
            simpleTipView.setWidth(220);
            simpleTipView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            simpleTipView.setFocusable(false);
            simpleTipView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
