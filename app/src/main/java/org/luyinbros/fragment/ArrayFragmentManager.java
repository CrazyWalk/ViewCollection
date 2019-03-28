package org.luyinbros.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArrayFragmentManager {

    private final String FRAGMENT_NAME;
    private FragmentManager mFragmentManager;

    public ArrayFragmentManager(FragmentManager fragmentManager,
                                int containerId) {
        mFragmentManager = fragmentManager;
        FRAGMENT_NAME = "ArrayFragmentManager" + "containerId" + containerId;
        IndexFragment fragment = getDelegateFragment();
        if (fragment == null) {
            fragment = new IndexFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("containerId", containerId);
            fragment.setArguments(bundle);
            mFragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_NAME)
                    .commitNow();
        } else {
            fragment.infoList = new ArrayList<>();
        }

    }


    public void registerFragment(Class fragmentClass) {
        IndexFragment fragment = getDelegateFragment();
        if (fragment != null) {
            fragment.infoList.add(new InnerFragmentInfo(fragmentClass));
        }

    }

    public void showFragment(int index) {
        IndexFragment fragment = getDelegateFragment();
        if (fragment != null) {
            fragment.showFragment(index);
        }
    }

    public int getCurrentIndex() {
        IndexFragment fragment = getDelegateFragment();
        return fragment != null ? fragment.currentIndex : -1;
    }

    @Nullable
    private IndexFragment getDelegateFragment() {
        return (IndexFragment) mFragmentManager.findFragmentByTag(FRAGMENT_NAME);
    }

    public static class IndexFragment extends Fragment {
        private ArrayList<FragmentInfo> infoList = new ArrayList<>();
        private int containerId;
        private int currentIndex = -1;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            if (bundle != null) {
                containerId = bundle.getInt("containerId");
            }
            if (savedInstanceState != null) {
                currentIndex = savedInstanceState.getInt("currentIndex");
            }
        }

        public void showFragment(int index) {
            if (index >= 0) {
                if (index != currentIndex) {
                    Fragment currentFragment = getCurrentFragment();
                    Fragment showFragment = getOrCreateFragment(index);
                    if (showFragment != null) {
                        if (showFragment != currentFragment) {
                            FragmentTransaction fragmentTransaction = resolveFragmentManager().beginTransaction();
                            if (!showFragment.isAdded()) {
                                fragmentTransaction.add(containerId, showFragment, infoList.get(index).getName());
                            }

                            if (currentFragment != null) {
                                currentFragment.setUserVisibleHint(false);
                                fragmentTransaction.hide(currentFragment);
                            }
                            showFragment.setUserVisibleHint(true);
                            fragmentTransaction.show(showFragment);
                            fragmentTransaction.commitNow();
                            currentIndex = index;

                        }
                    }
                }
            }
        }

        private FragmentManager resolveFragmentManager() {
            Fragment fragment = getParentFragment();
            if (fragment != null) {
                return fragment.getChildFragmentManager();
            } else {
                return getActivity().getSupportFragmentManager();
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("currentIndex", currentIndex);

        }

        @Nullable
        private Fragment getOrCreateFragment(int index) {
            Fragment fragment = resolveFragmentManager().findFragmentByTag(infoList.get(index).getName());
            if (fragment == null) {
                fragment = infoList.get(index).getFragment();
            }
            return fragment;
        }

        @Nullable
        private Fragment getCurrentFragment() {
            if (currentIndex >= 0) {
                return resolveFragmentManager().findFragmentByTag(infoList.get(currentIndex).getName());
            } else {
                return null;
            }
        }
    }

    private static class InnerFragmentInfo implements FragmentInfo, Serializable {
        private Class fragmentClass;


        public InnerFragmentInfo(Class fragmentClass) {
            this.fragmentClass = fragmentClass;
        }

        @Nullable
        @Override
        public Fragment getFragment() {
            try {
                return (Fragment) fragmentClass.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Class getFragmentClass() {
            return fragmentClass;
        }

        @Override
        public String getName() {
            return fragmentClass.getName();
        }

        @Override
        public int getLauncherMode() {
            return LAUNCHER_MODE_SINGLE_TASK;
        }
    }
}
