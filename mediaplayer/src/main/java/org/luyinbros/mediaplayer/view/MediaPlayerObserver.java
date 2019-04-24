package org.luyinbros.mediaplayer.view;

public interface MediaPlayerObserver {

    /**
     * 准备初始化
     */
    void onPreparing();

    /**
     * 初始化完成
     */
    void onPrepared();

    /**
     * 视频播放失败
     */
    void onError();

    /**
     * seekTo到指定位置播放，跳转成功后触发
     */
    void onSeekCompleted();

    /**
     * 播放结束
     */
    void onCompletion();

    /**
     * 开始缓冲
     */

    void onBufferStart();

    /**
     * 缓冲结束
     */
    void onBufferEnd();

    /**
     * 缓冲进度
     * @param percent
     */
    void onBuffering(int percent);
}
