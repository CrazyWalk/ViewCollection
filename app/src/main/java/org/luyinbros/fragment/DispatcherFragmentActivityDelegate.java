package org.luyinbros.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherFragmentActivityDelegate {
    private FragmentActivity activity;
    private FragmentManager mFragmentManager;
    private final int contentId;
    private Map<Class, FragmentInfo> mMap = new HashMap<>();

    public DispatcherFragmentActivityDelegate(FragmentActivity activity) {
        this.activity = activity;
        mFragmentManager = activity.getSupportFragmentManager();
        contentId = android.R.id.content;
    }

    public void registerFragmentInfo(FragmentInfo fragmentInfo) {
        mMap.put(fragmentInfo.getFragmentClass(), fragmentInfo);
    }

    public void navigate(Class fragmentClass, Bundle bundle) {
        FragmentInfo fragmentInfo = mMap.get(fragmentClass);
        if (fragmentInfo != null) {
            Fragment fragment = fragmentInfo.getFragment();
            mFragmentManager.
                    beginTransaction()
                    .add(android.R.id.content, fragment, fragmentInfo.getName())
                    .show(fragment)
                    .commitNow();
        }
    }


}
