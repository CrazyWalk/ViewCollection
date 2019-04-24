package org.luyinbros.demo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.netease.neliveplayer.playerkit.common.log.LogUtil;

import org.luyinbros.fragment.ArrayFragmentManager;
import org.luyinbros.mediaplayer.view.MediaPlayerObserver;
import org.luyinbros.mediaplayer.view.MediaPlayerView;
import org.luyinbros.widget.R;
import org.luyinbros.widget.Toasts;

import java.util.Locale;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    private MediaPlayerView mediaPlayerView;
    private View controlPanelView;
    private View playControlButton;
    private AppCompatSeekBar seekBar;
    private View fullScreenButton;
    private TextView timeTextView;
    private boolean isFullScreen = false;
    private static final int SHOW_PROGRESS = 0x01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long position;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    position = getProgress();
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (position % 1000));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mediaPlayerView = findViewById(R.id.mediaPlayerView);
        controlPanelView = findViewById(R.id.controlPanelView);
        playControlButton = findViewById(R.id.playControlButton);
        timeTextView = findViewById(R.id.timeTextView);
        seekBar = findViewById(R.id.seekBar);
        fullScreenButton = findViewById(R.id.fullScreenButton);

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    //获得当前窗体对象
                    Window window = getWindow();
                    //设置当前窗体为全屏显示
                    window.setFlags(flag, flag);

                    //改变屏幕方向（设置为横屏）
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                isFullScreen = !isFullScreen;
            }
        });
        playControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayerView.isPlaying()) {
                    mediaPlayerView.pause();
                } else {
                    mediaPlayerView.start();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTimeTextByProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayerView.seekTo(mediaPlayerView.getTotalDuration() * seekBar.getProgress() / 100);
            }
        });

        mediaPlayerView.setMediaPlayerObserver(new MediaPlayerObserver() {
            @Override
            public void onPreparing() {
                Log.d(TAG, "onPreparing: ");
            }

            @Override
            public void onPrepared() {
                Log.d(TAG, "onPrepared: ");
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onSeekCompleted() {
                Log.d(TAG, "onSeekCompleted: ");
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
            }

            @Override
            public void onCompletion() {
                Log.d(TAG, "onCompletion: ");
            }

            @Override
            public void onBufferStart() {
                Log.d(TAG, "onBufferStart: ");
            }

            @Override
            public void onBufferEnd() {
                Log.d(TAG, "onBufferEnd: ");
            }

            @Override
            public void onBuffering(int percent) {
                Log.d(TAG, "onBuffering: " + percent);
            }
        });

        mediaPlayerView.loadUrl("https://hifimedia.oss-cn-hangzhou.aliyuncs.com/media/2019041510354470");
        mediaPlayerView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayerView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private long getProgress() {

        int currentDuration = (int) mediaPlayerView.getCurrentDuration();
        int totalDuration = (int) mediaPlayerView.getTotalDuration();
        if (totalDuration > 0) {
            long pos = 100L * currentDuration / totalDuration;
            seekBar.setProgress((int) pos);
        }
        Log.d(TAG, "getProgress: " + currentDuration);
        updateTimeText(currentDuration);
        return currentDuration;
    }

    private void updateTimeText(long currentDuration) {
        timeTextView.setText(stringForTime(currentDuration) + "/"
                + stringForTime(mediaPlayerView.getTotalDuration()));
    }

    private void updateTimeTextByProgress(int progress) {
        long totalDuration = mediaPlayerView.getTotalDuration();
        timeTextView.setText(stringForTime(totalDuration * progress / 100) + "/"
                + stringForTime(totalDuration));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏
            //动态改变布局
            ViewGroup.LayoutParams layoutParams = mediaPlayerView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                mediaPlayerView.setLayoutParams(layoutParams);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //切换为竖屏显示（提前设置无效，只有现用现设置）
            int flagBack = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //获得当前窗体对象
            Window windowBack = this.getWindow();
            //设置当前窗体为全屏显示
            windowBack.clearFlags(flagBack);

            ViewGroup.LayoutParams layoutParams = mediaPlayerView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = 200;
                mediaPlayerView.setLayoutParams(layoutParams);
            }
        }
        super.onConfigurationChanged(newConfig);

    }


    private static String stringForTime(long position) {
        int totalSeconds = (int) ((position / 1000.0) + 0.5);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
    }
}
