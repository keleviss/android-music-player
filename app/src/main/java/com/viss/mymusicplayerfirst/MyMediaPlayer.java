package com.viss.mymusicplayerfirst;

import android.media.MediaPlayer;

public class MyMediaPlayer extends MediaPlayer{
    static MediaPlayer instance;

    public static MediaPlayer getInstance() {
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;
}
