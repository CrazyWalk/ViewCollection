package org.luyinbros.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class ActivityViewFragmentDelegate {
    private Fragment fragment;

    public ActivityViewFragmentDelegate(Fragment fragment) {
        this.fragment = fragment;
    }


    public void navigate(FragmentIntent intent) {
        DispatcherFragmentActivityDelegate activityDelegate = getDispatcherFragmentActivityDelegate();
        if (activityDelegate != null) {
            activityDelegate.navigate(intent);
        }
    }

    public void navigate(FragmentIntent intent, int requestCode) {
        DispatcherFragmentActivityDelegate activityDelegate = getDispatcherFragmentActivityDelegate();
        if (activityDelegate != null) {
            activityDelegate.navigate(intent, requestCode);
        }
    }

    public void finish() {
        DispatcherFragmentActivityDelegate activityDelegate = getDispatcherFragmentActivityDelegate();
        if (activityDelegate != null) {
            activityDelegate.finish(fragment);
        }
    }

    public void finish(int resultCode, Intent intent) {

    }


    @Nullable
    private DispatcherFragmentActivityDelegate getDispatcherFragmentActivityDelegate() {
        Activity activity = fragment.getActivity();
        if (activity instanceof DispatcherFragmentActivity) {
            return ((DispatcherFragmentActivity) activity).getDispatcherFragmentActivityDelegate();
        } else {
            return null;
        }
    }

    public void onAttach(Context context) {

    }

    public void onCreate(@Nullable Bundle savedInstanceState) {

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroyView() {

    }

    public void onDestroy() {

    }

    public void onDetach() {

    }

}
