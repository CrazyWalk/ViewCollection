package org.luyinbros.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;

public class DispatcherFragmentActivityDelegate {
    private FragmentActivity mActivity;
    private final int contentId;
    private DispatcherFragmentManifest manifest;

    public DispatcherFragmentActivityDelegate(@NonNull DispatcherFragmentActivity activity,
                                              @NonNull DispatcherFragmentManifest manifest) {
        this.mActivity = (FragmentActivity) activity;
        contentId = android.R.id.content;
        this.manifest = manifest;
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
        if (savedInstanceState == null) {
            launch();
        }
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

    @Nullable
    private FragmentManager getFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    private void launch() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            DispatcherFragmentManifest.FragmentInfo fragmentInfo = manifest.getLauncherFragmentInfo();
            if (fragmentInfo != null) {
                Fragment fragment = getOrCreateFragment(fragmentInfo);
                if (fragment != null) {
                    fragmentManager.beginTransaction()
                            .replace(contentId, fragment)
                            .commit();
                }
            }
        }
    }

    //TODO SINGLE TASK
    private Fragment getOrCreateFragment(@NonNull DispatcherFragmentManifest.FragmentInfo fragmentInfo) {
        try {
            return (Fragment) Class.forName(fragmentInfo.getName()).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
