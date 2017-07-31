package com.laojiang.lib_mediaplay.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.laojiang.lib_mediaplay.R;
import com.laojiang.lib_mediaplay.listener.NetWorkInfoStateListener;

/**
 * 类介绍（必填）：
 * Created by Jiang on 2017/7/27 9:04.
 */

public class NetWorkBroadReceiver extends BroadcastReceiver {
    private String resultString;
    private NetWorkInfoStateListener listener;
    private int netWorkType ;
    public static final int MOBILE_NET= 111;//只有移动数据
    public static final int WIFI_NET = 222;//只有wifi
    public static final int MOBILE_WIFI_NET = 333;//都有
    public static final int ALL_NO_NET = 444;//都没有

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * api 23以下
         */
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
               resultString = context.getString(R.string.net_all_have); netWorkType = MOBILE_WIFI_NET;
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                resultString = context.getString(R.string.net_only_wifi); netWorkType = WIFI_NET;
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                resultString =context.getString(R.string.net_only_mobile); netWorkType = MOBILE_NET;
            } else {
                resultString = context.getString(R.string.net_all_nohave); netWorkType = ALL_NO_NET;
            }

//API大于23时使用下面的方式进行网络监听
        }else {

            System.out.println("API level 大于23");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
//            for (int i=0; i < networks.length; i++){
//                //获取ConnectivityManager对象对应的NetworkInfo对象
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
//                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
//            }
            NetworkInfo networkInfoMobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo networkInfoWIfi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfoMobile.isConnected()&&networkInfoWIfi.isConnected()){//wifi 移动网络都连接
                resultString = context.getString(R.string.net_all_have); netWorkType = MOBILE_WIFI_NET;
            }else if (networkInfoMobile.isConnected()&&!networkInfoWIfi.isConnected()){//移动连接，wifi没有连接
                resultString =context.getString(R.string.net_only_mobile); netWorkType = MOBILE_NET;
            }else if (!networkInfoMobile.isConnected()&&networkInfoWIfi.isConnected()){//wifi链接，移动没有连接
                resultString = context.getString(R.string.net_only_wifi); netWorkType = WIFI_NET;
            }else if (!networkInfoMobile.isConnected()&&!networkInfoWIfi.isConnected()){//都没有连接
                resultString = context.getString(R.string.net_all_nohave); netWorkType = ALL_NO_NET;
            }
        }
        listener.callBackInternetInfo(resultString,netWorkType);
        Toast.makeText(context,resultString, Toast.LENGTH_SHORT).show();
    }
    public void setNetWorkBroadListener(NetWorkInfoStateListener listener){
        this.listener = listener;
    }
}
