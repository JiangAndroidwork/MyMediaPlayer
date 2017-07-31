package com.laojiang.mymediaplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.laojiang.lib_mediaplay.MediaPlayerView;
import com.laojiang.lib_mediaplay.VideoResourceBean;
import com.laojiang.lib_mediaplay.listener.NetWorkInfoStateListener;
import com.laojiang.lib_mediaplay.listener.PlayerErrorCallBack;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NetWorkInfoStateListener {

    private MediaPlayerView mediaPlayerView;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hindSystemUI();
        List<VideoResourceBean> list = new ArrayList<>();
//        list.add("http://114.215.83.40/cloudfile/public/microclass/files201707130846575996/苏州园林_20170713084657860.mp4");
//        list.add("http://114.215.83.40/cloudfile/public/microclass/files201707121530004016/哈哈_20170712153000150.mp4");
        list.add(new VideoResourceBean("http://114.215.83.40/cloudfile/public/microclass/files201707121530004016/哈哈_20170712153000150.mp4",false));
        list.add(new VideoResourceBean("http://114.215.83.40/cloudfile/public/microclass/files201707130846575996/苏州园林_20170713084657860.mp4",false));

        mediaPlayerView = (MediaPlayerView) findViewById(R.id.media_player);
        mediaPlayerView.setUrl(list);
        mediaPlayerView.setErrorListener(new PlayerErrorCallBack(){
            @Override
            public void playErrorInfo(String url, String message) {
                Toast.makeText(MainActivity.this,url+"\n"+message,Toast.LENGTH_SHORT).show();
            }
        });
//        mediaPlayerView.setNextVideo("http://114.215.83.40/cloudfile/public/microclass/files201707130846575996/苏州园林_20170713084657860.mp4");
        mediaPlayerView.setNetWorkInfoListener(this);
    }
    /**
     * 隐藏系统ui
     */
    private void hindSystemUI() {
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    /**
     * 显示系统ui
     */
    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 可以保证 自动隐藏
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //变成横屏了
            hindSystemUI();
            mediaPlayerView.setVideoParams(mediaPlayerView.getMediaPlayer(), true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //变成竖屏了
            hindSystemUI();
            mediaPlayerView.setVideoParams(mediaPlayerView.getMediaPlayer(), false);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerView.onDestory();
    }

    @Override
    protected void onResume() {
        mediaPlayerView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        mediaPlayerView.onPause();
        super.onPause();
    }

    @Override
    public void callBackInternetInfo(String msg, int code) {
        Log.i("接收到的网络状态==",msg+"---"+code);
    }
}
