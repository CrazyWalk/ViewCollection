package org.luyinbros.demo.dispatcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;


import org.luyinbros.fragment.DispatcherFragmentActivity;
import org.luyinbros.fragment.DispatcherFragmentActivityDelegate;


public class TestDispatcherFragmentActivity extends AppCompatActivity implements DispatcherFragmentActivity {
    private DispatcherFragmentActivityDelegate mDelegate = new DispatcherFragmentActivityDelegate(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);

    }

    @Override
    public DispatcherFragmentActivityDelegate getDispatcherFragmentActivityDelegate() {
        return mDelegate;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDelegate.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDelegate.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDelegate.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelegate.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatchTouchEvent = mDelegate.dispatchTouchEvent(ev);
        if (!dispatchTouchEvent) {
            dispatchTouchEvent = super.dispatchTouchEvent(ev);
        }
        return dispatchTouchEvent;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mDelegate.onRestart();
    }

}
