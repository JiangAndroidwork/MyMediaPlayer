package com.laojiang.lib_mediaplay.utils;

/**
 * 类介绍（必填）：时间工具
 * Created by Jiang on 2017/7/26 16:49.
 */

public class TimeUtils {
    private static String minute = "0";
    private static String second = "0";

    /**
     * @param time 传入单位 秒
     * @return
     */
    public static String getTime(long time) {//转换成string
        long m = time / 60;
        if (m > 1) {
            if (m < 10) {
                minute = "0" + m;
            } else {
                minute = m + "";
            }
        } else {
            minute = "00";
        }
        if ((time % 60) < 10) {
            second = "0" + (time % 60);
        } else {

            second = time % 60 + "";
        }
        return minute + ":" + second;
    }
}
