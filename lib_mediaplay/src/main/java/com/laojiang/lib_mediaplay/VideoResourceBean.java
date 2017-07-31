package com.laojiang.lib_mediaplay;

import android.view.SurfaceHolder;

/**
 * 类介绍（必填）：视频基类
 * Created by Jiang on 2017/7/24 13:06.
 */

public class VideoResourceBean {
    private int PLAYER_MODEL = 1;//0  循环播放  1 列表循环  2顺序播放一次
    private String videoPath;//视频路径
    private String videoName;//视频名称
    private long videoSize;//视频大小
    private boolean isLoop;//是否重播
    private SurfaceHolder surfaceHolder;//surfaceHolder
    private boolean isLocal ;//是否是本地视频..

    @Override
    public String toString() {
        return "VideoResourceBean{" +
                "PLAYER_MODEL=" + PLAYER_MODEL +
                ", videoPath='" + videoPath + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoSize=" + videoSize +
                ", isLoop=" + isLoop +
                ", surfaceHolder=" + surfaceHolder +
                ", isLocal=" + isLocal +
                '}';
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public VideoResourceBean(){

    }

    /**
     * 构造方法
     * @param videoPath
     * @param isLocal  是否是本地视频
     */
    public VideoResourceBean(String videoPath, boolean isLocal) {
        this.videoPath = videoPath;
        this.isLocal = isLocal;
    }

    public VideoResourceBean(SurfaceHolder surfaceHolder, String videoPath) {
        this.videoPath = videoPath;
        this.surfaceHolder = surfaceHolder;
    }





    public int getPLAYER_MODEL() {
        return PLAYER_MODEL;
    }

    public void setPLAYER_MODEL(int PLAYER_MODEL) {
        this.PLAYER_MODEL = PLAYER_MODEL;
    }



    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }
}
