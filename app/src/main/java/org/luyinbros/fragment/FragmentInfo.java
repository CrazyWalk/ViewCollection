package org.luyinbros.fragment;

import android.support.v4.app.Fragment;

public interface FragmentInfo {
    int LAUNCHER_MODE_STANDARD = 0;
    int LAUNCHER_MODE_SINGLE_TASK = 1;

    Fragment getFragment();

    String getName();

    int getLauncherMode();

    Class getFragmentClass();
}
