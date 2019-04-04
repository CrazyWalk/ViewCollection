package org.luyinbros.update;

import android.app.Application;
import android.app.DownloadManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.disposables.Disposable;


public class AppUpdater {
    private volatile static AppUpdater mInstance;
    private Application application;
    private ApkManager<DefaultAppUpdateInfo> mApkManager;
    private OnDownloadApkListener<DefaultAppUpdateInfo> mOnDownloadListener;


    private AppUpdater(Application application) {
        this.application = application;
        mApkManager = new DefaultApkManager(application);
    }

    public static AppUpdater getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AppUpdater.class) {
                if (mInstance==null){
                    mInstance = new AppUpdater((Application) context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public void autoCheckVersion(LifecycleOwner lifecycleOwner, FragmentManager fragmentManager) {
        new AutoCheckVersionLifecycleObserver(lifecycleOwner, this, fragmentManager);
    }


    public void checkVersion(LifecycleOwner lifecycleOwner, OnAppVersionCheckListener<DefaultAppUpdateInfo> onAppVersionCheckListener) {
        new CheckVersionLifecycleObserver(lifecycleOwner, this, onAppVersionCheckListener);
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
            e.printStackTrace();
        }

    }

    void startUpdateInBackground(final DefaultAppUpdateInfo updateInfo) {
        File file = mApkManager.getDownloadApkFile(updateInfo);
        if (file != null) {
            install(updateInfo);
        } else {
            if (mOnDownloadListener == null) {
                mOnDownloadListener = new OnDownloadApkListener<DefaultAppUpdateInfo>() {
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onSuccess(DownloadApkInfo<DefaultAppUpdateInfo> info) {
                        install(updateInfo);
                        unregisterDownloadApkListener(mOnDownloadListener);
                        mOnDownloadListener = null;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        unregisterDownloadApkListener(mOnDownloadListener);
                        mOnDownloadListener = null;
                    }

                };
                registerDownloadApkListener(mOnDownloadListener);
                mApkManager.downloadApk(updateInfo);
            }

        }
    }

    public void showUpdateDialog(@Nullable DefaultAppUpdateInfo appUpdateInfo,
                                 FragmentManager fragmentManager) {
        if (appUpdateInfo != null && appUpdateInfo.isUpdate()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("appUpdateInfo", appUpdateInfo);
           // DialogFactory.showDialog(new AppUpdateDialog(), fragmentManager, bundle, "AppUpdateDialog");
        }

    }

    private static String getVersionName(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        } else {
            return "";
        }

    }

    @Nullable
    private static PackageInfo getPackageInfo(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        PackageInfo pi;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (Exception e) {
            return null;
        }
        return pi;
    }


    private static class CheckVersionLifecycleObserver implements LifecycleObserver {
        private LifecycleOwner mLifecycleOwner;
        private AppUpdateSession<DefaultAppUpdateInfo> mSession;
        private AppUpdater mUpdater;
        private OnAppVersionCheckListener<DefaultAppUpdateInfo> mListener;

        private CheckVersionLifecycleObserver(LifecycleOwner lifecycleOwner,
                                              AppUpdater updater,
                                              OnAppVersionCheckListener<DefaultAppUpdateInfo> listener) {
            this.mLifecycleOwner = lifecycleOwner;
            this.mUpdater = updater;
            mSession = mUpdater.openSession();
            mListener = listener;
            lifecycleOwner.getLifecycle().addObserver(this);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume(LifecycleOwner owner) {
            if (!mSession.isCheckingVersion()) {
                mSession.checkVersion(new OnAppVersionCheckListener<DefaultAppUpdateInfo>() {
                    @Override
                    public void onStart() {
                        mListener.onStart();
                    }

                    @Override
                    public void onSuccess(DefaultAppUpdateInfo data) {
                        mListener.onSuccess(data);
                        mSession.destroy();
                        mLifecycleOwner.getLifecycle().removeObserver(CheckVersionLifecycleObserver.this);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        mListener.onFailure(e);
                        mSession.destroy();
                        mLifecycleOwner.getLifecycle().removeObserver(CheckVersionLifecycleObserver.this);
                    }
                });
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy(LifecycleOwner owner) {
            mSession.destroy();
            mLifecycleOwner.getLifecycle().removeObserver(this);
        }
    }

    private static class AutoCheckVersionLifecycleObserver implements LifecycleObserver {
        private LifecycleOwner mLifecycleOwner;
        private AppUpdateSession<DefaultAppUpdateInfo> mSession;
        private AppUpdater mUpdater;
        private FragmentManager mFragmentManager;

        private AutoCheckVersionLifecycleObserver(LifecycleOwner lifecycleOwner, AppUpdater updater, FragmentManager fragmentManager) {
            this.mLifecycleOwner = lifecycleOwner;
            lifecycleOwner.getLifecycle().addObserver(this);
            this.mUpdater = updater;
            mSession = mUpdater.openSession();
            mFragmentManager = fragmentManager;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume(LifecycleOwner owner) {
            if (!mSession.isCheckingVersion()) {
                mSession.checkVersion(new OnAppVersionCheckListener<DefaultAppUpdateInfo>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(DefaultAppUpdateInfo data) {
                        mUpdater.showUpdateDialog(data, mFragmentManager);
                        mSession.destroy();
                        mLifecycleOwner.getLifecycle().removeObserver(AutoCheckVersionLifecycleObserver.this);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        mSession.destroy();
                        mLifecycleOwner.getLifecycle().removeObserver(AutoCheckVersionLifecycleObserver.this);
                    }
                });
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy(LifecycleOwner owner) {
            mSession.destroy();
            mLifecycleOwner.getLifecycle().removeObserver(this);
        }
    }

    private static class DefaultApkManager implements ApkManager<DefaultAppUpdateInfo> {
        private Context mContext;
        private Long taskId;
        private DownloadManager mDownloadManager;
        private List<OnDownloadApkListener<DefaultAppUpdateInfo>> onDownloadApkListenerList = new ArrayList<>();
        private Handler mHandler = new Handler();
        private int mProgressing = -1;
        private Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (taskId != null) {
                    DownloadManager.Query query = new DownloadManager.Query().setFilterById(taskId);
                    Cursor c = mDownloadManager.query(query);
                    if (c.moveToFirst()) {
                        int downloadBytesIdx = c.getColumnIndexOrThrow(
                                DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                        int totalBytesIdx = c.getColumnIndexOrThrow(
                                DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                        long totalBytes = c.getLong(totalBytesIdx);
                        long downloadBytes = c.getLong(downloadBytesIdx);
                        int percent = (int) (downloadBytes * 100 / totalBytes);
                        if (percent < 100) {
                            onProgress(percent);
                            mHandler.postDelayed(this, 500);
                        } else {
                            mHandler.removeCallbacks(this);
                        }
                    }
                }


            }
        };

        DefaultApkManager(Context context) {
            this.mContext = context.getApplicationContext();
            mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        @Override
        public File getDownloadApkFile(DefaultAppUpdateInfo info) {
            File downloadedApk = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    targetSubFile(info));
            if (downloadedApk.isFile()) {
                return downloadedApk;
            }
            return null;
        }

        @Override
        public Uri getUriForFile(DefaultAppUpdateInfo info) {
            File file = getDownloadApkFile(info);
            if (file != null) {
                return getUriForFile(file);
            }
            return null;
        }

        @Override
        public void downloadApk(DefaultAppUpdateInfo info) {
            File targetFile = getDownloadApkFile(info);
            if (targetFile != null) {
                onSuccess(info, targetFile);
            } else {
                try {
                    if (taskId == null) {
                        Uri uri = Uri.parse(info.getApkUrl());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                        request.setTitle("音乐圣经");
                        request.setDescription("");
                        request.setMimeType("application/vnd.android.package-archive");
                        request.setAllowedOverRoaming(false);
                        request.setVisibleInDownloadsUi(true);
                        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, targetSubFile(info));
                        taskId = mDownloadManager.enqueue(request);
                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        filter.addAction("android.intent.action.VIEW_DOWNLOADS");
                        mContext.registerReceiver(new DefaultApkManager.DownloadReceiver(info), filter);
                        onProgress(0);
                        mHandler.postDelayed(mRunnable, 500);
                    }
                } catch (Exception e) {
                    onFailure(e);
                }

            }
        }

        @Override
        public boolean isDownloading() {
            return taskId != null;
        }

        @Override
        public void registerDownloadApkListener(OnDownloadApkListener<DefaultAppUpdateInfo> listener) {
            onDownloadApkListenerList.add(listener);
        }

        @Override
        public void unregisterDownloadApkListener(OnDownloadApkListener<DefaultAppUpdateInfo> listener) {
            onDownloadApkListenerList.remove(listener);
        }

        private String createApkFileName(DefaultAppUpdateInfo appUpdateInfo) {
            return "musicbible_" + appUpdateInfo.getNewestVersion();
        }

        private String targetSubFile(DefaultAppUpdateInfo appUpdateInfo) {
            return "musicbible" + File.separator + createApkFileName(appUpdateInfo);
        }

        private Uri getUriForFile(File file) {
            //获取当前app的包名
            String FPAuth = mContext.getPackageName() + ".fileprovider";
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(mContext, FPAuth, file);
            } else {
                uri = Uri.fromFile(file);
            }
            return uri;
        }

        private String getFilePathByTaskId(long id) {
            String filePath = null;
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor cursor = mDownloadManager.query(query);
            while (cursor.moveToNext()) {
                filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            }
            cursor.close();
            return filePath;
        }

        private void onSuccess(DefaultAppUpdateInfo info, File apkFile) {
            for (OnDownloadApkListener<DefaultAppUpdateInfo> onDownloadApkListener : onDownloadApkListenerList) {
                onDownloadApkListener.onSuccess(new DefaultDownloadApkInfo(info, apkFile));
            }
            taskId = null;

        }

        private void onProgress(int progress) {
            this.mProgressing = progress;
            for (OnDownloadApkListener<DefaultAppUpdateInfo> onDownloadApkListener : onDownloadApkListenerList) {
                onDownloadApkListener.onProgress(progress);
            }
        }

        private void onFailure(Exception e) {
            for (OnDownloadApkListener<DefaultAppUpdateInfo> onDownloadApkListener : onDownloadApkListenerList) {
                onDownloadApkListener.onFailure(e);
            }
        }

        public class DownloadReceiver extends BroadcastReceiver {
            private DefaultAppUpdateInfo mExecuteInfo;

            public DownloadReceiver(DefaultAppUpdateInfo executeInfo) {
                this.mExecuteInfo = executeInfo;
            }

            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (taskId == reference) {
                    try {
                        onSuccess(mExecuteInfo, new File(getFilePathByTaskId(taskId)));
                    } catch (Exception e) {
                        onFailure(e);
                    }
                    mExecuteInfo = null;
                    mContext.unregisterReceiver(this);
                }
            }
        }
    }

    public static class DefaultAppUpdateInfo implements AppUpdateInfo {
        private String apkUrl;
        private String minVersion;
        private String newestVersion;
        private String updateDescription;
        private String currentVersion;
        private boolean isUpdate = false;

        public DefaultAppUpdateInfo() {

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.apkUrl);
            dest.writeString(this.minVersion);
            dest.writeString(this.newestVersion);
            dest.writeString(this.updateDescription);
            dest.writeString(this.currentVersion);
            dest.writeInt(this.isUpdate ? 0 : 1);
        }


        protected DefaultAppUpdateInfo(Parcel in) {
            this.apkUrl = in.readString();
            this.minVersion = in.readString();
            this.newestVersion = in.readString();
            this.updateDescription = in.readString();
            this.currentVersion = in.readString();
            this.isUpdate = in.readInt() == 1;
        }

        public static final Creator<DefaultAppUpdateInfo> CREATOR = new Creator<DefaultAppUpdateInfo>() {
            @Override
            public DefaultAppUpdateInfo createFromParcel(Parcel source) {
                return new DefaultAppUpdateInfo(source);
            }

            @Override
            public DefaultAppUpdateInfo[] newArray(int size) {
                return new DefaultAppUpdateInfo[size];
            }
        };

        public boolean isUpdate() {
            return isUpdate;
        }

        public boolean isForceUpdate() {
            return false;
        }

        public String getUpdateDescription() {
            return updateDescription;
        }

        public String getNewestVersion() {
            return newestVersion;
        }

        public String getApkUrl() {
            return apkUrl;
        }
    }

    private static class DefaultAppUpdateSession implements AppUpdateSession<DefaultAppUpdateInfo> {
        private AppUpdater mAppUpdater;
        private boolean isChecking;
        private Disposable mDisposable;
        private OnAppVersionCheckListener<DefaultAppUpdateInfo> mListener;
        //private RemoteV2AppRepository mRemoteAppRepository;
        private OnDownloadApkListener<DefaultAppUpdateInfo> mDownloadListener = new OnDownloadApkListener<DefaultAppUpdateInfo>() {

            @Override
            public void onProgress(int progress) {
                if (mListener != null) {
                    mDisposable.dispose();
                    mListener.onFailure(new IllegalStateException());
                    mListener = null;
                }
            }

            @Override
            public void onSuccess(DownloadApkInfo<DefaultAppUpdateInfo> info) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };

        DefaultAppUpdateSession(AppUpdater appUpdater) {
            this.mAppUpdater = appUpdater;
            mAppUpdater.registerDownloadApkListener(mDownloadListener);
          //  mRemoteAppRepository = RepositoryV2FactoryClient.getRemoteRepositoryFactory(appUpdater.application).appRepository();
        }

        @Override
        public void checkVersion(final OnAppVersionCheckListener<DefaultAppUpdateInfo> listener) {
            if (!isChecking) {
                mListener = listener;
                isChecking = true;
                if (mAppUpdater.isDownloadingApk()) {
                    listener.onFailure(new IllegalStateException());
                    isChecking = false;
                } else {
//                    checkUpdateObservable(mRemoteAppRepository, mAppUpdater.application)
//                            .subscribe(new Observer<DefaultAppUpdateInfo>() {
//                                @Override
//                                public void onSubscribe(Disposable d) {
//                                    mDisposable = d;
//                                    listener.onStart();
//                                }
//
//                                @Override
//                                public void onNext(DefaultAppUpdateInfo info) {
//                                    listener.onSuccess(info);
//                                    isChecking = false;
//                                    mDisposable = null;
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    listener.onFailure(e);
//                                    isChecking = false;
//                                    mDisposable = null;
//                                }
//
//                                @Override
//                                public void onComplete() {
//
//                                }
//                            });
                }
            }
        }

        @Override
        public boolean isCheckingVersion() {
            return isChecking;
        }

        @Override
        public void destroy() {
            mAppUpdater.unregisterDownloadApkListener(mDownloadListener);
            if (mDisposable != null) {
                mDisposable.dispose();
            }
        }


    }

    public static class DefaultDownloadApkInfo implements DownloadApkInfo<AppUpdater.DefaultAppUpdateInfo> {
        private AppUpdater.DefaultAppUpdateInfo defaultAppUpdateInfo;
        private File file;

        public DefaultDownloadApkInfo(AppUpdater.DefaultAppUpdateInfo defaultAppUpdateInfo, File file) {
            this.defaultAppUpdateInfo = defaultAppUpdateInfo;
            this.file = file;
        }

        @Override
        public AppUpdater.DefaultAppUpdateInfo getUpdateInfo() {
            return defaultAppUpdateInfo;
        }

        @Override
        public File getApkFile() {
            return file;
        }
    }

}
