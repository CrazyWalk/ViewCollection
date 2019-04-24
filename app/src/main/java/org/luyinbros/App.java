package org.luyinbros;

import android.app.Application;

import org.luyinbros.mediaplayer.view.MediaPlayerModule;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MediaPlayerModule.init(this);
    }
}
