package com.laojiang.lib_mediaplay.content;

/**
 * 类介绍（必填）：
 * Created by Jiang on 2017/7/19 11:21.
 */

public class PlayerContent {
    public static  boolean IS_INFO_ERROR = false; //播放错误
    public static int PLAY_STATE = 1;// 0 开始 1暂停 2结束
    public static int SCREEN_STATE = 0;//0竖屏 1横屏

    public static int COTROLLER_LAYOUT_STATE = 1;//0 隐藏控制栏 1 显示控制栏
    public static Boolean IS_SCROLL_SEEKBAR = false;//true正在拖动进度条,false 没有拖动

    public static String EXCEPTION_START = "exception_start";//开始播放的 异常标记
    public static String EXCEPTION_INFO = "exception_info";//视频异常
    public static String EXCEPTION_STOP ="exception_stop";//定制播放异常标记

    public static boolean IS_BUTTON_START = true;//是按钮点击的暂停， false不是
}
