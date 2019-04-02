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
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

    //TODO 显示
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


    private static class DefaultApkManager implements ApkManager<DefaultAppUpdateInfo> {
        private Context mContext;
        private Long taskId;
        private DownloadManager mDownloadManager;
        private List<OnDownloadApkListener<DefaultAppUpdateInfo>> onDownloadApkListenerList = new ArrayList<>();


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
                        request.setTitle("别买了更新");
                        request.setDescription("");
                        request.setMimeType("application/vnd.android.package-archive");
                        request.setAllowedOverRoaming(false);
                        request.setVisibleInDownloadsUi(true);
                        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, targetSubFile(info));
                        taskId = mDownloadManager.enqueue(request);

                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        filter.addAction("android.intent.action.VIEW_DOWNLOADS");
                        mContext.registerReceiver(new DefaultApkManager.DownloadReceiver(info), filter);
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
            return "musicbible_" + appUpdateInfo.getLastUpdate();
        }

        private String targetSubFile(DefaultAppUpdateInfo appUpdateInfo) {
            return "musicbible" + File.separator + createApkFileName(appUpdateInfo);
        }

        private Uri getUriForFile(File file) {
            //获取当前app的包名
            String FPAuth = mContext.getPackageName() + ".fileProvider";
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
        private String lastUpdate;


        public String getApkUrl() {
            return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.apkUrl);
            dest.writeString(this.lastUpdate);
        }

        public DefaultAppUpdateInfo() {
        }

        protected DefaultAppUpdateInfo(Parcel in) {
            this.apkUrl = in.readString();
            this.lastUpdate = in.readString();
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
    }

    private static class DefaultAppUpdateSession implements AppUpdateSession<DefaultAppUpdateInfo> {
        private AppUpdater mAppUpdater;
        private boolean isChecking;
        private Disposable mDisposable;
        private OnAppVersionCheckListener<DefaultAppUpdateInfo> mListener;

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
                    Observable.timer(5000, TimeUnit.MILLISECONDS)
                            .flatMap(new Function<Long, ObservableSource<DefaultAppUpdateInfo>>() {
                                @Override
                                public ObservableSource<DefaultAppUpdateInfo> apply(Long aLong) throws Exception {
                                    DefaultAppUpdateInfo defaultAppUpdateInfo = new DefaultAppUpdateInfo();
                                    return Observable.just(defaultAppUpdateInfo);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<DefaultAppUpdateInfo>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    mDisposable = d;
                                    listener.onStart();
                                }

                                @Override
                                public void onNext(DefaultAppUpdateInfo info) {
                                    listener.onSuccess(info);
                                    isChecking = false;
                                    mDisposable = null;
                                }

                                @Override
                                public void onError(Throwable e) {
                                    listener.onFailure(e);
                                    isChecking = false;
                                    mDisposable = null;
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
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
