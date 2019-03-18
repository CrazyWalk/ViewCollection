package org.luyinbros.rx;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public class CombineDisposable implements Disposable {
    private Map<String, Disposable> mMap = new HashMap<>();

    /**
     * 添加RxJava 可断开对象
     *
     * @param key        对应的键值
     * @param disposable 断开的对象
     */
    public void addDisposable(String key, @Nullable Disposable disposable) {
        dispose(key);
        if (disposable != null) {
            mMap.put(key, disposable);
        }

    }

    /**
     * 断开指定的键
     *
     * @param key 断开指定的键
     */
    public void dispose(String key) {
        Disposable disposable = mMap.get(key);
        if (disposable != null) {
            dispose(disposable);
            mMap.remove(key);
        }
    }

    /**
     * 指定的键是否断开
     *
     * @param key 指定的键
     */
    public boolean isDisposable(String key) {
        return isDisposed(mMap.get(key));

    }

    @Override
    public void dispose() {
        for (Map.Entry<String, Disposable> entry : mMap.entrySet()) {
            Disposable disposable = entry.getValue();
            dispose(disposable);
        }
        mMap.clear();
    }


    @Override
    public boolean isDisposed() {
        if (mMap.size() == 0) {
            return true;
        } else {
            for (Map.Entry<String, Disposable> entry : mMap.entrySet()) {
                Disposable disposable = entry.getValue();
                if (!isDisposed(disposable)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isDisposed(Disposable d) {
        return d == null || d.isDisposed();
    }

    private static void dispose(Disposable d) {
        if (!isDisposed(d)) {
            d.dispose();
        }
    }
}
