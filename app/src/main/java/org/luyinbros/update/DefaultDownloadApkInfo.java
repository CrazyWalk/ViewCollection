package org.luyinbros.update;

import org.luyinbros.update.core.DownloadApkInfo;

import java.io.File;

public class DefaultDownloadApkInfo implements DownloadApkInfo<DefaultAppUpdateInfo> {
    private DefaultAppUpdateInfo defaultAppUpdateInfo;
    private File file;

    public DefaultDownloadApkInfo(DefaultAppUpdateInfo defaultAppUpdateInfo, File file) {
        this.defaultAppUpdateInfo = defaultAppUpdateInfo;
        this.file = file;
    }

    @Override
    public DefaultAppUpdateInfo getUpdateInfo() {
        return defaultAppUpdateInfo;
    }

    @Override
    public File getApkFile() {
        return file;
    }
}
