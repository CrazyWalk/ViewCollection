package org.luyinbros.update.core;

public interface OnAppVersionCheckListener<T extends AppUpdateInfo> {

    void onStart();

    void onSuccess(T data);

    void onFailure(Throwable e);
}
