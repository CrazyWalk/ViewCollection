package org.luyinbros.mediaplayer.view;

import android.content.Context;
import android.support.annotation.NonNull;

import com.netease.neliveplayer.playerkit.sdk.PlayerManager;
import com.netease.neliveplayer.playerkit.sdk.model.SDKOptions;

public class MediaPlayerModule {

    //TODO 异步初始化
    public static void init(@NonNull Context context) {
        SDKOptions config = new SDKOptions();
        //是否开启动态加载功能，默认关闭
        //        config.dynamicLoadingConfig = new NEDynamicLoadingConfig();
        //        config.dynamicLoadingConfig.isDynamicLoading = true;
        //        config.dynamicLoadingConfig.isArmeabiv7a = true;
        //        config.dynamicLoadingConfig.armeabiv7aUrl = "your_url";
        //        config.dynamicLoadingConfig.onDynamicLoadingListener = mOnDynamicLoadingListener;
        // SDK将内部的网络请求以回调的方式开给上层，如果需要上层自己进行网络请求请实现config.dataUploadListener，
        // 如果上层不需要自己进行网络请求而是让SDK进行网络请求，这里就不需要操作config.dataUploadListener
        // config.dataUploadListener = mOnDataUploadListener;
        //是否支持H265解码回调
        // config.supportDecodeListener = mOnSupportDecodeListener;
        //这里可以绑定客户的账号系统或device_id，方便出问题时双方联调
        // config.thirdUserId = "your_id";
        PlayerManager.init(context.getApplicationContext(), config);
    }
}
