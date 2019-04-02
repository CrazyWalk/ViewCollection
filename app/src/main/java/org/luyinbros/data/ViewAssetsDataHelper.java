package org.luyinbros.data;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.Nullable;

public class ViewAssetsDataHelper {
    private static volatile SoftReference<List<Address>> mAddressCache;

    private static volatile Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static Observable<List<Address>> getAddress(Context context) {
        final Application application = (Application) context.getApplicationContext();
        List<Address> cacheList = getCacheAddressList();
        if (cacheList == null) {
            return Observable.create(new ObservableOnSubscribe<List<Address>>() {
                @Override
                public void subscribe(ObservableEmitter<List<Address>> emitter) throws Exception {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(application.getAssets().open("districts.json")));
                    String next = "";
                    while (true) {
                        next = bufferedReader.readLine();
                        if (next != null && next.length() != 0) {
                            sb.append(next);
                        } else {
                            break;
                        }
                    }
                    List<Address> dataList = getGson().fromJson(sb.toString(), new TypeToken<List<Address>>() {
                    }.getType());
                    mAddressCache = new SoftReference<>(dataList);
                    emitter.onNext(dataList);
                    emitter.onComplete();
                }
            });
        } else {
            return Observable.just(cacheList);
        }

    }

    @Nullable
    private static List<Address> getCacheAddressList() {
        if (mAddressCache == null) {
            return null;
        }
        return mAddressCache.get();
    }

}
