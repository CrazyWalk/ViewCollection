package org.luyinbros.update;

public interface AppUpdateSession<T extends AppUpdateInfo> {

    boolean isCheckingVersion();

    void checkVersion(OnAppVersionCheckListener<T> listener);

    void destroy();
}
