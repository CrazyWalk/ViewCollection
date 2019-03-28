package org.luyinbros.demo.dispatcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.luyinbros.demo.dispatcher.home.HomeFragment;
import org.luyinbros.fragment.DispatcherFragmentActivityDelegate;
import org.luyinbros.fragment.FragmentInfo;
import org.luyinbros.widget.R;

public class DispatcherFragmentActivity extends AppCompatActivity {
    private DispatcherFragmentActivityDelegate mDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate = new DispatcherFragmentActivityDelegate(this);
        registerAll();
        if (savedInstanceState == null) {
            mDelegate.navigate(HomeFragment.class, null);
        }


    }

    private void registerAll() {
        mDelegate.registerFragmentInfo(new DefaultFragmentInfo(HomeFragment.class));
    }

    private static class DefaultFragmentInfo implements FragmentInfo {
        private Class fragmentClass;

        public DefaultFragmentInfo(Class fragmentClass) {
            this.fragmentClass = fragmentClass;
        }

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
        public String getName() {
            return fragmentClass.getName();
        }

        @Override
        public int getLauncherMode() {
            return LAUNCHER_MODE_STANDARD;
        }

        @Override
        public Class getFragmentClass() {
            return fragmentClass;
        }
    }

}
