package org.luyinbros.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import org.luyinbros.update.core.ApkManager;
import org.luyinbros.update.core.OnDownloadApkListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class DefaultApkManager implements ApkManager<DefaultAppUpdateInfo> {
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
                    mContext.registerReceiver(new DownloadReceiver(info), filter);
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
