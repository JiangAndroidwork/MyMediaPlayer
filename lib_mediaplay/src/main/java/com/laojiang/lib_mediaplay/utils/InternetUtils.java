package com.laojiang.lib_mediaplay.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

/**
 * 类介绍（必填）：网络工具类
 * Created by Jiang on 2017/7/19 15:16.
 */

public class InternetUtils {

    /**
     * 获取流量
     * @param context
     * @return
     */
    public static long getInternetSpeed(Context context){
        long l = 0;
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            //转为KB
            l = TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return l;
    }


}
