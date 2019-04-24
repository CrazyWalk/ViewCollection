package org.luyinbros.mediaplayer.view;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface MediaPlayerController {

    /**
     * 载入视频资源
     *
     * @param url 视频资源地址，如果为null 则关闭上一个视频播放。当前没有播放
     */
    void loadUrl(@Nullable String url);

    /**
     * 是否正在播放
     *
     * @return true 正在播放 false 已经停止
     */
    boolean isPlaying();

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 控制器处于这个状态的时候调用
     */
    void onResume();

    /**
     * 控制器处于stop状态时候调用
     */
    void onStop();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 是否设置静音
     *
     * @param mute true 静音 false 取消静音
     */
    void setMute(boolean mute);

    /**
     * 获取视频的总长度
     *
     * @return 当前视频的总长度
     */
    long getTotalDuration();

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    long getCurrentDuration();

    /**
     * 跳到指定的时间
     *
     * @param duration 时间点
     */
    void seekTo(long duration);


}
