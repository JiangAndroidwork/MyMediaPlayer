package com.laojiang.lib_mediaplay;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import com.laojiang.lib_mediaplay.content.PlayerContent;
import com.laojiang.lib_mediaplay.model.MediaPlayerManage;

/**
 * 类介绍（必填）：播放异常处理
 * Created by Jiang on 2017/7/20 10:56.
 */

public class PlayerException extends IllegalStateException {
    private static final String TAG = "PlayerException";
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private int position;
    private String videoInfoErr ="视频来源错误";

    public String getVideoInfoErr() {
        return videoInfoErr;
    }

    public PlayerException(){

    }



    public PlayerException(String message) {
        super(message);
        this.surfaceHolder = surfaceHolder;
        init(message);
    }

    public PlayerException(String s, int position) {
        super(s);
        this.position = position;
        init(s);
    }

    private void init(String s) {
        if (s.equals(PlayerContent.EXCEPTION_START)&&!PlayerContent.IS_INFO_ERROR){//开始播放 异常
            MediaPlayer mediaPlayer = MediaPlayerManage.getInstance().getMediaPlayer();
            if (mediaPlayer==null){
                mediaPlayer = new MediaPlayer();
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.seekTo(position);
                        mediaPlayer.start();
                    }
                });
            }else {

            }

        }else if (s.equals(PlayerContent.EXCEPTION_INFO)){//视频源异常
            Log.i(TAG,"视频资源异常");
        }else if (s.equals(PlayerContent.EXCEPTION_STOP)){//停止

        }
    }
}
