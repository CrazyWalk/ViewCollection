package org.luyinbros.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public final class DispatcherFragmentManifest {
    private Map<String, FragmentInfo> fragmentMap = new HashMap<>();
    private FragmentInfo launcherFragmentInfo;

    public void registerFragment(FragmentInfo fragmentInfo) {
        fragmentMap.put(fragmentInfo.name, fragmentInfo);
    }

    public void setLauncherFragmentInfo(FragmentInfo launcherFragmentInfo) {
        this.launcherFragmentInfo = launcherFragmentInfo;
        registerFragment(launcherFragmentInfo);
    }

    @Nullable
    FragmentInfo getLauncherFragmentInfo() {
        return launcherFragmentInfo;
    }

    @Nullable
    FragmentInfo getFragmentInfo(String name) {
        return fragmentMap.get(name);
    }

    @Nullable
    FragmentInfo getFragmentInfo(Class cls) {
        return fragmentMap.get(cls.getName());
    }


    public static class FragmentInfo {
        public static int LAUNCHER_MODE_STANDARD = 0;
        public static int LAUNCHER_MODE_SINGLE_TASK = 1;
        public static int LAUNCHER_MODE_SINGLE_TOP = 2;

        private String name;
        private int launchMode = LAUNCHER_MODE_STANDARD;


        public static FragmentInfo create(String clsName) {
            FragmentInfo fragmentInfo = new FragmentInfo();
            fragmentInfo.name = clsName;
            return fragmentInfo;
        }

        public static FragmentInfo create(Class cls) {
            FragmentInfo fragmentInfo = new FragmentInfo();
            fragmentInfo.name = cls.getName();
            return fragmentInfo;
        }


        public String getName() {
            return name;
        }

        public int getLaunchMode() {
            return launchMode;
        }
    }
}
