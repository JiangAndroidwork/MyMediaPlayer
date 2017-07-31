package com.laojiang.lib_mediaplay.listener;

import com.laojiang.lib_mediaplay.VideoResourceBean;

/**
 * 类介绍（必填）：播放器控制接口
 * Created by Jiang on 2017/7/19 8:39.
 */

public interface PlayerControlListener {
    void start();//开始
    void pause();//暂停
    void stop();//停止
    void reset();//重置
    void next(VideoResourceBean bean);



}
