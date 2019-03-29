package org.luyinbros.update.core;

import java.io.File;

public interface DownloadApkInfo<T extends AppUpdateInfo> {

    T getUpdateInfo();

    File getApkFile();
}
