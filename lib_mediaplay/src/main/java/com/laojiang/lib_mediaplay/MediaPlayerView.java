package com.laojiang.lib_mediaplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laojiang.lib_mediaplay.broadcast.NetWorkBroadReceiver;
import com.laojiang.lib_mediaplay.content.PlayerContent;
import com.laojiang.lib_mediaplay.listener.NetWorkInfoStateListener;
import com.laojiang.lib_mediaplay.listener.PlayerControlListener;
import com.laojiang.lib_mediaplay.listener.PlayerErrorListener;
import com.laojiang.lib_mediaplay.listener.PlayerStateListener;
import com.laojiang.lib_mediaplay.listener.ScreenSwitchListener;
import com.laojiang.lib_mediaplay.model.MediaPlayerManage;
import com.laojiang.lib_mediaplay.utils.TimeUtils;
import com.laojiang.lib_mediaplay.weight.PlayerWindowInfoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类介绍（必填）：播放视频主view
 * Created by Jiang on 2017/7/18 16:26.
 */

public class MediaPlayerView extends FrameLayout implements SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener, PlayerControlListener, ScreenSwitchListener, PlayerWindowInfoView.InfoCallBack, PlayerStateListener, NetWorkInfoStateListener {

    private static final String TAG = "MediaPlayerView";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaControllerView mediaControllerView;
    private LayoutParams layout_control;
    private TextView tvTime;
    private String url = "";
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Activity activity;
    private TimerTask task;
    private long hindTime = 4000;//自动隐藏时间
    private LayoutParams layoutInfo;
    private PlayerWindowInfoView playerWindowInfoView;
    private int currentSeekBarPosition;
    private boolean isMove;
    private Timer timerPlayer;
    private PlayerErrorListener listenerError;
    private TimerTask playerTask;
    private List<VideoResourceBean> list = new ArrayList<>();//播放视频的资源集合
    private int currentVideoPosition;//当前视频播放的位置。
    private LayoutParams layout_surface;
    private boolean isLocalVideo = true;//是否是本地视频
    private VideoResourceBean videoResourceBean;
    private TimerTask taskNew;
    private ImageView ivBack;
    private NetWorkInfoStateListener listener;//网络信息回调接口
    private NetWorkBroadReceiver netWorkBroadReceiver;
    private IntentFilter intentFilter;
    private  int code;
    public MediaPlayerView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public MediaPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.activity = (Activity) context;
        init(context, attrs);
    }

    public MediaPlayer getMediaPlayer() {
        return MediaPlayerManage.getInstance().getMediaPlayer();
    }

    private void init(Context context, AttributeSet attrs) {
        initXML(context, attrs);
        layout_surface = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView = new SurfaceView(context, attrs);
        surfaceView.setLayoutParams(layout_surface);
        //控制
        layout_control = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mediaControllerView = new MediaControllerView(context);
        layout_control.gravity = Gravity.BOTTOM;
        mediaControllerView.setLayoutParams(layout_control);
        //屏幕中间 信息显示
        layoutInfo = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playerWindowInfoView = new PlayerWindowInfoView(context, this);
        layoutInfo.gravity = Gravity.CENTER;
        playerWindowInfoView.setLayoutParams(layoutInfo);

        //退出按钮：
        LayoutParams layout_Back = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_Back.gravity = Gravity.TOP;
        layout_Back.width = 100;
        layout_Back.height = 100;
        layout_Back.setMargins(50, 50, 0, 0);
        ivBack = new ImageView(context);
        ivBack.setImageResource(R.drawable.icon_close);
        ivBack.setLayoutParams(layout_Back);

        this.setBackgroundColor(Color.BLACK);
        this.addView(surfaceView);
        this.addView(mediaControllerView);
        playerWindowInfoView.setVisibility(GONE);
        this.addView(playerWindowInfoView);
        this.addView(ivBack);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        mediaControllerView.setAllEnable_False();
        initListener();
    }

    private void initXML(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MediaPlayerView, 0, 0);
        int anInt = a.getInt(R.styleable.MediaPlayerView_transitionScreentImage, R.drawable.icon_bt_previous);
        url = a.getString(R.styleable.MediaPlayerView_resourceUrl);

        a.recycle();
    }

    //设置 视频播放类型 本地or网路
    public void setVideoType(boolean b) {
        this.isLocalVideo = b;

    }


    /**
     * 设置路径
     *
     * @param url
     */
    public void setUrl(List<VideoResourceBean> url) {
        if (surfaceHolder != null) {
            for (VideoResourceBean bean : url) {
                bean.setSurfaceHolder(surfaceHolder);
                list.add(bean);
            }
        }
    }


    private void initListener() {
        seekBar = mediaControllerView.getSeekBar();
        seekBar.setOnSeekBarChangeListener(this);

        tvTime = mediaControllerView.getTvTime();
        mediaControllerView.setScreenListener(this);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestory();
                activity.finish();
            }
        });
    }

    /**
     * 错误监听回调
     *
     * @param errorListener
     */
    public void setErrorListener(final PlayerErrorListener errorListener) {
        this.listenerError = errorListener;

    }

    /**
     * 实时更新进度条状态
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                int position = MediaPlayerManage.getInstance().getCurrentMediaplayerPositiono();
                int mMax = MediaPlayerManage.getInstance().getTotalMediaplayerLength();
                int sMax = seekBar.getMax();
                seekBar.setProgress(position);
                tvTime.setText(TimeUtils.getTime(position / 1000) + "/" + TimeUtils.getTime(mMax / 1000));
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.i("异常出现了===", "时间任务--" + e.getMessage());
//                if (timerPlayer != null) timerPlayer.cancel();
            }
        }
    };





    /***
     * 核心 代码  surfaceCreated home返回手机桌面 再进入应用会调用。。
     * @param surfaceHolder
     */
    //surfaceview
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


        if (MediaPlayerManage.getInstance().getMediaPlayer() == null) {
            videoResourceBean = list.get(currentVideoPosition);//注册监听
            if (!videoResourceBean.isLocal()) playerWindowInfoView.initView();
            MediaPlayerManage.getInstance().createMediaPlayer(surfaceHolder, list.size() > 0 ? videoResourceBean.getVideoPath() : url);
            //缓存监听
//        MediaPlayerManage.getInstance().setUpdateListener();
            MediaPlayerManage.getInstance().setErrorListError(listenerError);
            //完成状态监听
            MediaPlayerManage.getInstance().setPreparedListener(this);
        } else {
            MediaPlayerManage.getInstance().getMediaPlayer().setDisplay(surfaceHolder);
            if (MediaPlayerManage.getInstance().getCurrentMediaplayerPositiono() > 3000)//回退3秒
                MediaPlayerManage.getInstance().getMediaPlayer().seekTo(MediaPlayerManage.getInstance().getCurrentMediaplayerPositiono() - 3);
            start();
            StartProgress();
            if (!videoResourceBean.isLocal()) playerWindowInfoView.setMediaPlayer();
        }
    }

    /**
     * 设置视频宽高比
     */
    private void initVideoParams() {
        //先设置视频播放的大小
        try {
            setVideoParams(MediaPlayerManage.getInstance().getMediaPlayer(), getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        } catch (Exception e) {
            throw new PlayerException(PlayerContent.EXCEPTION_START);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (MediaPlayerManage.getInstance().getMediaPlayer().isPlaying()) {
            Log.i("是否运行----", "运行了");
            pause();
        }
//        mediaPlayer.release();
        if (timerPlayer != null) timerPlayer.cancel();
        if (!videoResourceBean.isLocal()) playerWindowInfoView.unSetMediaPlayer();
    }

    //准备好了。。。
    @Override
    public void prepared(MediaPlayer mediaPlayer) {
        Log.i("准备完成呢过了----", "完成了");

        if (!videoResourceBean.isLocal()) {
            activity.registerReceiver(netWorkBroadReceiver,intentFilter);
            if (code==NetWorkBroadReceiver.MOBILE_NET){//只有移动网络
                initOnlyMoblie();
            }
            mediaControllerView.setInfoViewListener(playerWindowInfoView);
            playerWindowInfoView.setMediaPlayer();
            if (playerWindowInfoView.getVisibility() == GONE)
                playerWindowInfoView.setVisibility(VISIBLE);
        } else {
            if (playerWindowInfoView.getVisibility() == VISIBLE)
                playerWindowInfoView.setVisibility(GONE);
            playerWindowInfoView.unSetMediaPlayer();
        }
        initVideoParams();
        seekBar.setMax(mediaPlayer.getDuration());
        mediaControllerView.setAllEnablle_True();//设置控制按钮显示或可按
        mediaControllerView.setStateListener(MediaPlayerView.this);
        StartProgress();
        Log.i("videoResourceBean===", videoResourceBean.toString());

        start();
    }

    //播放完成了。。。。
    @Override
    public void complete(MediaPlayer mediaPlayer) {
        Log.i("完成了====", "是的=");

        next(null);
        if (MediaPlayerManage.getInstance().getMediaPlayer() != null && currentVideoPosition == list.size() - 1) {//最后一个视频
            MediaPlayerManage.getInstance().getMediaPlayer().seekTo(0);
            pause();
        }
//        next(null);

    }

    private Handler hindLayoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hindLayoutAnimation();
        }
    };

    /**
     * 设置网络状态接口
     *
     * @param listener
     */
    public void setNetWorkInfoListener(NetWorkInfoStateListener listener) {
        this.listener = listener;
    }


    //seekbar
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        PlayerContent.IS_SCROLL_SEEKBAR = true;
        Log.i("进度条开始拖拽===", "开始");
        if (!videoResourceBean.isLocal()) {
            playerWindowInfoView.setMediaPlayer();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MediaPlayerManage.getInstance().getMediaPlayer().seekTo(seekBar.getProgress());
        PlayerContent.IS_SCROLL_SEEKBAR = false;
        Log.i("进度条停止拖拽监听===", "停止");

    }

    //播放动作
    @Override
    public void start() {
        Log.i(TAG, "开始播放====");
        MediaPlayerManage.getInstance().start();
        mediaControllerView.getBtPlayerState().setImageResource(R.drawable.icon_bt_pause);
    }

    // 中间信息显示网速和 缓存百分比
    //缓冲状态
    @Override
    public void loadingState() {

        if (MediaPlayerManage.getInstance().getMediaPlayer().isPlaying()) {
            Log.i(TAG, "缓冲状态===============");
            pause();
            mediaControllerView.getBtPlayerState().setImageResource(R.drawable.icon_bt_pause);
            mediaControllerView.getBtPlayerState().setEnabled(true);
        }
    }

    //暂停状态
    @Override
    public void pauseState() {
        Log.i(TAG, "暂停状态===============");
        mediaControllerView.getBtPlayerState().setEnabled(true);
        start();

    }

    //播放状态
    @Override
    public void playingState() {
        Log.i(TAG, "播放状态===============");
        if (!MediaPlayerManage.getInstance().getMediaPlayer().isPlaying()) {
//                        start();
//                        PlayerContent.PLAY_STATE = 0;
//                        mediaControllerView.getBtPlayerState().setImageResource(R.drawable.icon_bt_pause);
        }
        mediaControllerView.getBtPlayerState().setEnabled(true);
    }


    @Override
    public void pause() {

        MediaPlayerManage.getInstance().pause();
        mediaControllerView.getBtPlayerState().setImageResource(R.drawable.icon_bt_start);
    }

    @Override
    public void stop() {
        MediaPlayerManage.getInstance().stop();
        mediaControllerView.getBtPlayerState().setImageResource(R.drawable.icon_bt_start);
    }

    @Override
    public void reset() {
        if (MediaPlayerManage.getInstance().getMediaPlayer() != null) {
            MediaPlayerManage.getInstance().getMediaPlayer().reset();
        }
    }

    /**
     * 下一个
     */
    @Override
    public void next(VideoResourceBean bean) {
        if (list.size() > 0 && currentVideoPosition < list.size() - 1) {//播放下一个视频
            currentVideoPosition = currentVideoPosition + 1;
            videoResourceBean = list.get(currentVideoPosition);
            timerPlayer.cancel();//取消时间任务，因为 在播放下一个视频的时候  会让mediaplayer为null,这时候因为任务中有获取mediaplayer.getDuration，所以会报异常。
            if (!videoResourceBean.isLocal()) playerWindowInfoView.initView();
            MediaPlayerManage.getInstance().next(videoResourceBean);

            if (seekBar != null) {
                seekBar.setProgress(0);

            }
            mediaControllerView.setAllEnable_False();
        } else if (list.size() > 0 && currentVideoPosition == list.size() - 1) {
            Toast.makeText(activity, activity.getString(R.string.last_video), Toast.LENGTH_SHORT).show();

        } else if (list.size() <= 0) {
            Toast.makeText(activity, activity.getString(R.string.request_add_video), Toast.LENGTH_SHORT).show();
        }
        Log.i("下一个视频====", "点击响应");

    }

    /**
     * 点击屏幕 隐藏显示 控制栏
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://点击屏幕
                if (PlayerContent.COTROLLER_LAYOUT_STATE == 0) {
                    showControlLayoutAnimation();
                } else {
                    hindLayoutHandler.sendEmptyMessage(0);
                }

                break;
        }

        return super.onTouchEvent(event);
    }

    private void showControlLayoutAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mediaControllerView, "translationY", 100, 0);
        objectAnimator.setDuration(500);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                PlayerContent.COTROLLER_LAYOUT_STATE = 1;
                Log.i("状态条是否拖动===", PlayerContent.IS_SCROLL_SEEKBAR + "");
            }
        });
    }


    public void hindLayoutAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mediaControllerView, "translationY", 0, 100);
        objectAnimator.setDuration(500);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                PlayerContent.COTROLLER_LAYOUT_STATE = 0;
            }
        });
    }

    public void StartProgress() {
        if (timerPlayer != null) timerPlayer.cancel();
        timerPlayer = new Timer();
        playerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timerPlayer.schedule(playerTask, 0, 10);
    }

    //横竖屏
    @Override
    public void verticalScreen() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void horizontalScreen() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }




    public void onDestory() {
        Log.i(TAG, "生命周期onDestory===");
        if (MediaPlayerManage.getInstance().getMediaPlayer() != null && MediaPlayerManage.getInstance().getMediaPlayer().isPlaying()) {
            MediaPlayerManage.getInstance().getMediaPlayer().stop();
            MediaPlayerManage.getInstance().getMediaPlayer().release();
        }
        if (timerPlayer != null)
            timerPlayer.cancel();
        if (!videoResourceBean.isLocal()) playerWindowInfoView.unSetMediaPlayer();
    }

    public void onResume() {
        if (netWorkBroadReceiver==null) {
            netWorkBroadReceiver = new NetWorkBroadReceiver();
        }
        netWorkBroadReceiver.setNetWorkBroadListener(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

    }
    public void onPause(){
        activity.unregisterReceiver(netWorkBroadReceiver);
    }
    /**
     *  网络状态监听回调
     * @param msg
     * @param code
     */
    @Override
    public void callBackInternetInfo(String msg, int code) {
        listener.callBackInternetInfo(msg,code);
        this.code = code;
    }

    /**
     * 当只有移动网络的时候
     */
    private void initOnlyMoblie() {
            if (playerWindowInfoView.playerType.equals(PlayerWindowInfoView.PLAYING_STATE)
                    | playerWindowInfoView.playerType.equals(PlayerWindowInfoView.LOADING_STATE)) {
                playerWindowInfoView.showStartIcon();
            }
            playerWindowInfoView.unSetMediaPlayer();
            pause();
        PlayerContent.PLAY_STATE = 1;
        PlayerContent.IS_BUTTON_START = false;
        mediaControllerView.getBtPlayerState().setImageResource(R.drawable.icon_bt_start);
    }

    /**
     * 设置SurfaceView的参数
     *
     * @param mediaPlayer
     * @param isLand
     */
    public void setVideoParams(MediaPlayer mediaPlayer, boolean isLand) {
        //获取surfaceView父布局的参数
        ViewGroup.LayoutParams rl_paramters = this.getLayoutParams();
        //获取SurfaceView的参数

        //设置宽高比为16/9
        float screen_widthPixels = getResources().getDisplayMetrics().widthPixels;
        float screen_heightPixels = getResources().getDisplayMetrics().widthPixels * 9f / 16f;
        //取消全屏
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (isLand) {
            screen_heightPixels = getResources().getDisplayMetrics().heightPixels;
            //设置全屏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
//            rl_paramters.width = (int) screen_widthPixels;
//            rl_paramters.height = (int) screen_heightPixels;

        //获取MediaPlayer的宽高
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        Log.i(TAG, "视频宽和高==" + videoWidth + "---" + videoHeight);
        float video_por = videoWidth / videoHeight;
        float screen_por = screen_widthPixels / screen_heightPixels;
        //16:9    16:12

        layout_surface.height = (int) ((screen_widthPixels * videoHeight) / videoWidth);
        layout_surface.width = LayoutParams.MATCH_PARENT;

        if (videoHeight > videoWidth) {
            layout_surface.height = LayoutParams.MATCH_PARENT;
            layout_surface.width = LayoutParams.MATCH_PARENT;
        }
        layout_surface.gravity = Gravity.CENTER;
        rl_paramters.width = LayoutParams.MATCH_PARENT;//getResources().getDisplayMetrics().widthPixels 留出了虚拟按键的高度，而LayoutParams.MATCH_PARENT包含了虚拟按键的高度，填充整个屏幕
        rl_paramters.height = LayoutParams.MATCH_PARENT;

        this.setLayoutParams(rl_paramters);
        surfaceView.setLayoutParams(layout_surface);
    }


}
