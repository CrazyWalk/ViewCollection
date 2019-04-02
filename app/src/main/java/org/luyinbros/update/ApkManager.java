package org.luyinbros.update;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.File;

public interface ApkManager<T extends AppUpdateInfo> {

    @Nullable
    File getDownloadApkFile(T info);

    @Nullable
    Uri getUriForFile(T info);

    void downloadApk(T info);

    boolean isDownloading();

    void registerDownloadApkListener(OnDownloadApkListener<T> listener);

    void unregisterDownloadApkListener(OnDownloadApkListener<T> listener);
}
