package com.laojiang.lib_mediaplay.listener;

import android.media.MediaPlayer;

/**
 * 类介绍（必填）：播放错误接口
 * Created by Jiang on 2017/7/19 8:44.
 */

public interface PlayerErrorListener {
    void playError(MediaPlayer mediaPlayer);
    void playErrorInfo(String url,String message);
}
