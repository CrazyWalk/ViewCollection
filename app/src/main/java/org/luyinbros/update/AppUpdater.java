package org.luyinbros.update;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;

import org.luyinbros.update.core.ApkManager;
import org.luyinbros.update.core.AppUpdateSession;
import org.luyinbros.update.core.OnAppVersionCheckListener;
import org.luyinbros.update.core.OnDownloadApkListener;

public class AppUpdater {
    private volatile static AppUpdater mInstance;
    private Application application;
    private ApkManager<DefaultAppUpdateInfo> mApkManager;

    private AppUpdater(Application application) {
        this.application = application;
        mApkManager = new DefaultApkManager(application);
    }

    public static AppUpdater getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppUpdater.class) {
                mInstance = new AppUpdater((Application) context.getApplicationContext());
            }
        }
        return mInstance;
    }

    public void autoCheckVersion(LifecycleOwner lifecycleOwner, FragmentManager fragmentManager) {
        new AutoCheckLifecycleObserver(lifecycleOwner, this, fragmentManager);
    }


    public void checkVersion(LifecycleOwner lifecycleOwner, OnAppVersionCheckListener onAppVersionCheckListener) {

    }

    public boolean isDownloadingApk() {
        return mApkManager.isDownloading();
    }

    public AppUpdateSession<DefaultAppUpdateInfo> openSession() {
        return new DefaultAppUpdateSession(this);
    }

    public void registerDownloadApkListener(OnDownloadApkListener<DefaultAppUpdateInfo> listener) {
        mApkManager.registerDownloadApkListener(listener);
    }

    public void unregisterDownloadApkListener(OnDownloadApkListener<DefaultAppUpdateInfo> listener) {
        mApkManager.unregisterDownloadApkListener(listener);
    }

    public void install(DefaultAppUpdateInfo appUpdateInfo) {
        try {
            Intent startIntent = new Intent();
            startIntent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setDataAndType(mApkManager.getUriForFile(appUpdateInfo), "application/vnd.android.package-archive");
            application.startActivity(startIntent);
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }


    public void showUpdateDialog(DefaultAppUpdateInfo appUpdateInfo,
                                 FragmentManager fragmentManager) {

    }

    private static class AutoCheckLifecycleObserver implements LifecycleObserver {
        private LifecycleOwner mLifecycleOwner;
        private AppUpdateSession<DefaultAppUpdateInfo> mSession;
        private AppUpdater mUpdater;
        private FragmentManager mFragmentManager;

        private AutoCheckLifecycleObserver(LifecycleOwner lifecycleOwner, AppUpdater updater, FragmentManager fragmentManager) {
            this.mLifecycleOwner = lifecycleOwner;
            lifecycleOwner.getLifecycle().addObserver(this);
            this.mUpdater = updater;
            mSession = mUpdater.openSession();
            mFragmentManager = fragmentManager;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            if (!mSession.isCheckingVersion()) {
                mSession.checkVersion(new OnAppVersionCheckListener<DefaultAppUpdateInfo>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(DefaultAppUpdateInfo data) {
                        mUpdater.showUpdateDialog(data, mFragmentManager);
                    }

                    @Override
                    public void onFailure(Throwable e) {

                    }
                });
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            mSession.destroy();
            mLifecycleOwner.getLifecycle().removeObserver(this);
        }
    }
}
