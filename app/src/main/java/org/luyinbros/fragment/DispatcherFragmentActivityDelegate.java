package org.luyinbros.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yokeyword.fragmentation.ISupportActivity;

public class DispatcherFragmentActivityDelegate {
    private FragmentActivity mActivity;
    private final int contentId;

    public DispatcherFragmentActivityDelegate(DispatcherFragmentActivity activity) {
        this.mActivity = (FragmentActivity) activity;
        contentId = android.R.id.content;
    }

    public void navigate(FragmentIntent intent) {

    }

    public void navigate(FragmentIntent intent, int requestCode) {

    }

    public void finish(Fragment fragment) {

    }

    public boolean onBackPressed() {
        return true;
    }


    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }


    public void onCreate(@Nullable Bundle savedInstanceState) {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public void onRestart() {

    }

    public void onSaveInstanceState(Bundle outState) {

    }


}
