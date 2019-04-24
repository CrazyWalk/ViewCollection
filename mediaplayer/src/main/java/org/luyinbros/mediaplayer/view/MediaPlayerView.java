package org.luyinbros.mediaplayer.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.netease.neliveplayer.playerkit.sdk.LivePlayerObserver;
import com.netease.neliveplayer.playerkit.sdk.PlayerManager;
import com.netease.neliveplayer.playerkit.sdk.VodPlayer;
import com.netease.neliveplayer.playerkit.sdk.VodPlayerObserver;
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo;
import com.netease.neliveplayer.playerkit.sdk.model.StateInfo;
import com.netease.neliveplayer.playerkit.sdk.model.VideoBufferStrategy;
import com.netease.neliveplayer.playerkit.sdk.model.VideoOptions;
import com.netease.neliveplayer.playerkit.sdk.model.VideoScaleMode;
import com.netease.neliveplayer.playerkit.sdk.view.AdvanceTextureView;

public class MediaPlayerView extends FrameLayout implements MediaPlayerController {
    private AdvanceTextureView mTextureView;
    private VodPlayer mPlayer;
    private VideoOptions mVideoOptions;
    private boolean isMute = false;
    private InnerLivePlayerObserver mObserver;

    public MediaPlayerView(Context context) {
        this(context, null);
    }

    public MediaPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextureView = new AdvanceTextureView(context, attrs, defStyleAttr);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mTextureView, layoutParams);
    }

    public void setMediaPlayerObserver(MediaPlayerObserver observer) {
        if (mObserver == null) {
            mObserver = new InnerLivePlayerObserver();
        }
        mObserver.observer = observer;
        if (mPlayer != null && mObserver.observer != null) {
            mPlayer.registerPlayerObserver(mObserver, true);
        }
    }


    @Override
    public void loadUrl(String url) {

        releasePlayer();
        if (!TextUtils.isEmpty(url)) {
            if (mVideoOptions == null) {
                //TODO VideoOptions 需要转移
                mVideoOptions = new VideoOptions();
                mVideoOptions.hardwareDecode = true;
                mVideoOptions.isPlayLongTimeBackground = false;
                mVideoOptions.bufferStrategy = VideoBufferStrategy.ANTI_JITTER;
            }
            mPlayer = PlayerManager.buildVodPlayer(getContext(), url, mVideoOptions);
            mPlayer.setupRenderView(mTextureView, VideoScaleMode.FIT);
            mPlayer.setMute(isMute);
            if (mObserver != null) {
                setMediaPlayerObserver(mObserver.observer);
            }

        }
    }


    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public void start() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    @Override
    public void destroy() {
        releasePlayer();
    }

    @Override
    public void onResume() {
        if (mPlayer!=null){
            mPlayer.onActivityResume(false);
        }
    }

    @Override
    public void onStop() {
        if (mPlayer!=null){
            mPlayer.onActivityStop(false);
        }
    }

    @Override
    public void setMute(boolean mute) {
        this.isMute = mute;
        if (mPlayer != null) {
            mPlayer.setMute(mute);
        }
    }

    @Override
    public long getTotalDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public long getCurrentDuration() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return -1;
    }

    @Override
    public void seekTo(long duration) {
        if (mPlayer != null) {
            mPlayer.seekTo(duration);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releasePlayer();
    }

    //TODO 退出app在进来一段时间会闪退
    private void releasePlayer() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.setupRenderView(null, VideoScaleMode.NONE);
        mPlayer.registerPlayerObserver(mObserver, false);
        mTextureView.releaseSurface();
        mTextureView = null;
        stop();
        mPlayer = null;

    }

//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//
//        if (mPlayer != null) {
//            if (visibility == GONE) {
//                mPlayer.onActivityStop(false);
//            } else {
//                mPlayer.onActivityResume(false);
//            }
//        }
//        super.onWindowVisibilityChanged(visibility);
//    }

    private static class InnerLivePlayerObserver implements VodPlayerObserver {
        private MediaPlayerObserver observer;

        private InnerLivePlayerObserver() {

        }

        @Override
        public void onPreparing() {
            if (observer != null) {
                observer.onPreparing();
            }
        }

        @Override
        public void onPrepared(MediaInfo mediaInfo) {
            if (observer != null) {
                observer.onPrepared();
            }
        }

        @Override
        public void onError(int code, int extra) {
            if (observer != null) {
                observer.onError();
            }
        }

        @Override
        public void onFirstVideoRendered() {

        }

        @Override
        public void onFirstAudioRendered() {

        }

        @Override
        public void onBufferingStart() {
            if (observer != null) {
                observer.onBufferStart();
            }
        }

        @Override
        public void onBufferingEnd() {
            if (observer != null) {
                observer.onBufferEnd();
            }
        }

        @Override
        public void onBuffering(int percent) {
            if (observer != null) {
                observer.onBuffering(percent);
            }
        }

        @Override
        public void onVideoDecoderOpen(int value) {

        }

        @Override
        public void onStateChanged(StateInfo stateInfo) {

        }

        @Override
        public void onHttpResponseInfo(int code, String header) {

        }

        @Override
        public void onCurrentPlayProgress(long currentPosition, long duration, float percent, long cachedPosition) {

        }

        @Override
        public void onSeekCompleted() {
            if (observer != null) {
                observer.onSeekCompleted();
            }
        }

        @Override
        public void onCompletion() {
            if (observer != null) {
                observer.onCompletion();
            }
        }

        @Override
        public void onAudioVideoUnsync() {

        }

        @Override
        public void onNetStateBad() {

        }

        @Override
        public void onDecryption(int ret) {

        }
    }
}
