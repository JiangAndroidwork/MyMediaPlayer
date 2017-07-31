package com.laojiang.lib_mediaplay.listener;

import android.media.MediaPlayer;

/**
 * 类介绍（必填）：播放状态接口
 * Created by Jiang on 2017/7/19 8:46.
 */

public interface PlayerStateListener {
    void prepared(MediaPlayer mediaPlayer);
    void complete(MediaPlayer mediaPlayer);
}
