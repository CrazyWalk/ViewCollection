package org.luyinbros.update.core;

import android.net.Uri;
import android.support.annotation.Nullable;

import org.luyinbros.update.DefaultAppUpdateInfo;

import java.io.File;

public interface ApkManager<T extends AppUpdateInfo> {

    @Nullable
    File getDownloadApkFile(T info);

    @Nullable
    Uri getUriForFile(T info);

    void downloadApk(T info);

    boolean isDownloading();

    void registerDownloadApkListener(OnDownloadApkListener<DefaultAppUpdateInfo> listener);

    void unregisterDownloadApkListener(OnDownloadApkListener<DefaultAppUpdateInfo> listener);
}
