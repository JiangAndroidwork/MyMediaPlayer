package com.laojiang.lib_mediaplay.model;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.laojiang.lib_mediaplay.PlayerException;
import com.laojiang.lib_mediaplay.VideoResourceBean;
import com.laojiang.lib_mediaplay.content.PlayerContent;
import com.laojiang.lib_mediaplay.listener.PlayerControlListener;
import com.laojiang.lib_mediaplay.listener.PlayerErrorListener;
import com.laojiang.lib_mediaplay.listener.PlayerStateListener;

import java.io.IOException;

/**
 * 类介绍（必填）：播放器管理类
 * Created by Jiang on 2017/7/19 8:51.
 */

public class MediaPlayerManage  implements PlayerControlListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "MediaPlayerManage";
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private static MediaPlayerManage mediaPlayerManage;
    private MediaPlayer mediaPlayer;
    private PlayerControlListener playerControlListener;
    private String url;

    private PlayerStateListener preparedListener;//状态接口 用于返回mediaplayer的是否 准备好 是否 完成的接口
    private MediaPlayerManage() {

    }

    public static synchronized MediaPlayerManage getInstance() {
        if (mediaPlayerManage == null) {
            mediaPlayerManage = new MediaPlayerManage();
        }
        return mediaPlayerManage;
    }

    /**
     * 创建并准备mediaplayer
     *
     * @param surfaceHolder
     * @param url
     * @return
     */
    public MediaPlayer createMediaPlayer(SurfaceHolder surfaceHolder, String url) {
        this.url = url;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
            try {

            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);//保持屏幕唤醒
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();

        }


        return mediaPlayer;
    }
    public void onPause(){
        mediaPlayer.setOnPreparedListener(null);
        mediaPlayer.setOnCompletionListener(null);
    }

    /**
     * 缓存监听
     */
    public void setUpdateListener() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                    Log.i("缓存监听===", i + "");
                }
            });


        }
    }

    public void setErrorListError(final PlayerErrorListener errorListener){
        if (mediaPlayer!=null){
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    switch (i) {
                        case -1004:
                            Log.d(TAG, "MEDIA_ERROR_IO");
                            break;
                        case -1007:
                            Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                            break;
                        case 200:
                            Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                            break;
                        case 100:
                            Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                            break;
                        case -110:
                            Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                            break;
                        case 1:
                            errorListener.playErrorInfo(url,new PlayerException(PlayerContent.EXCEPTION_INFO).getVideoInfoErr());
                            Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                            break;
                        case -1010:
                            Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                            break;
                    }
                    switch (i1) {
                        case 800:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                            break;
                        case 702:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                            break;
                        case 701:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 802:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 801:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                            break;
                        case 1:
                            Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                            break;
                        case 3:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                            break;
                        case 700:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                            break;
                    }

                    return false;
                }
            });
        }
    }


    @Override
    public void start() {
        PlayerContent.PLAY_STATE = 0;
        if (mediaPlayer != null&&!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
            } catch (IllegalStateException e) {
                throw new PlayerException(PlayerContent.EXCEPTION_START,mediaPlayer.getCurrentPosition());
            }
        }
    }

    @Override
    public void pause() {
        PlayerContent.PLAY_STATE = 1;
        Log.i("暂停监听===","执行了");
        if (mediaPlayer != null&&mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
            } catch (IllegalStateException e) {
                throw new PlayerException(PlayerContent.EXCEPTION_START,mediaPlayer.getCurrentPosition());
            }
        }
    }

    @Override
    public void stop() {
        PlayerContent.PLAY_STATE = 2;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void next(final VideoResourceBean bean) {
        Log.i(TAG,"下一个视频==="+"\n"+bean.toString());

        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
//            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer = null;

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createMediaPlayer(bean.getSurfaceHolder(),bean.getVideoPath());
            }
        },500);

    }




    /**
     *
     * @return 获取视频宽度
     */
    public int getVideoWidth(){
        if (mediaPlayer!=null){
            return mediaPlayer.getVideoWidth();
        }
        return -1;
    }

    /**
     *
     * @return 获取视频高度
     */
    public int getVideoHeight(){
        if (mediaPlayer!=null){
            return mediaPlayer.getVideoHeight();
        }
        return -1;
    }
    /**
     * 返回当前播放位置
     * @return
     */
    public int  getCurrentMediaplayerPositiono(){
        if (mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }else {
            return -1;
        }
    }

    /**
     * 返回 视频总长度
     * @return
     */
    public int getTotalMediaplayerLength(){
        if (mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }else {
            return -1;
        }
    }


    public void setPreparedListener(PlayerStateListener preparedListener){
        this.preparedListener = preparedListener;

    }
    //准备好了
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        preparedListener.prepared(mediaPlayer);
    }

    /**
     * 完成
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        preparedListener.complete(mediaPlayer);
    }
}
