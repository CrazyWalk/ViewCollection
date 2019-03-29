package org.luyinbros.update;

import org.luyinbros.update.core.AppUpdateSession;
import org.luyinbros.update.core.DownloadApkInfo;
import org.luyinbros.update.core.OnAppVersionCheckListener;
import org.luyinbros.update.core.OnDownloadApkListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

class DefaultAppUpdateSession implements AppUpdateSession<DefaultAppUpdateInfo> {
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
