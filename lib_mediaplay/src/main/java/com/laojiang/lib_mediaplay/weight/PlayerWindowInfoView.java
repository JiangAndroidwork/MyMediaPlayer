package com.laojiang.lib_mediaplay.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laojiang.lib_mediaplay.R;
import com.laojiang.lib_mediaplay.content.PlayerContent;
import com.laojiang.lib_mediaplay.model.MediaPlayerManage;
import com.laojiang.lib_mediaplay.utils.InternetUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 类介绍（必填）：视频播放信息显示 view
 * Created by Jiang on 2017/7/20 8:14.
 */

public class PlayerWindowInfoView extends RelativeLayout implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private static final String TAG = "PlayerWindowInfoView";
    public static final String LOADING_STATE = "loading_state";//正在缓冲状态
    public static final String PLAYING_STATE = "playing_state";//正在播放状态
    public static final String PAUSE_STATE = "pause_state";//暂停状态


    private ImageView ivPlayer;
    private int playerButtonWidth = 80;
    private int playerButtonHeight = 80;

    private int videoLength;
    private Timer timer;
    private TimerTask task;
    public  String playerType;//缓冲还是播放类型
    private float setLoadingValue;//设置缓冲的大小
    private float currentPosition;//获取当前视频播放的位置
    private int percentageOfLoading;//缓冲时候显示的百分比
    private float mediaplayerLoading ;//mediaplayer缓冲的大小
    private float y;//已缓冲与 当前播放位置的差值
    private ProgressBar progressBar;
    private TextView tvShowPercentage;
    private InfoCallBack infoCallBack;//开始按钮回调接口
    private Context context;
    private TextView tvInternetSpeed;
    private long lastTimeStamp;
    private long lastTotalRxBytes;
    private ViewGroup.LayoutParams layoutParams;

    public PlayerWindowInfoView(@NonNull Context context,InfoCallBack infoCallBack) {
        super(context);
        this.infoCallBack = infoCallBack;
        initView(context, null);
    }
    //动作状态接口。。
    public interface InfoCallBack{
        void start();//开始动作
        void loadingState();//正在缓存状态
        void pauseState();//暂停状态
        void playingState();//播放状态。。

    }



    private void initView(Context context, AttributeSet attrs) {
        this.context = context;
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(rl);

        //按钮
        LayoutParams layoutPlayerState = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivPlayer = new ImageView(context);
        layoutPlayerState.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

        layoutPlayerState.width = playerButtonWidth/4;
        layoutPlayerState.height = playerButtonHeight/4;
        ivPlayer.setLayoutParams(layoutPlayerState);
        //progressbar
        LayoutParams layoutProgress = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar = new ProgressBar(context);
        layoutProgress.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        layoutPlayerState.width = 160;
        layoutPlayerState.height = 160;
        progressBar.setLayoutParams(layoutProgress);

        //
        LayoutParams layoutTextView = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvShowPercentage = new TextView(context);
        layoutTextView.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        tvShowPercentage.setTextSize(15);
        tvShowPercentage.setTextColor(context.getColor(R.color.white));
        tvShowPercentage.setText("0%");
       tvShowPercentage.setLayoutParams(layoutTextView);

        relativeLayout.setId(111);
        relativeLayout.addView(ivPlayer);
        relativeLayout.addView(progressBar);
        relativeLayout.addView(tvShowPercentage);
        //网速
        RelativeLayout.LayoutParams layoutTvInternetSpeed = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvInternetSpeed = new TextView(context);

        layoutTvInternetSpeed.addRule(RelativeLayout.BELOW,relativeLayout.getId());
        layoutTvInternetSpeed.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tvInternetSpeed.setTextSize(10);
        tvInternetSpeed.setTextColor(context.getColor(R.color.white));
        tvInternetSpeed.setLayoutParams(layoutTvInternetSpeed);


        ivPlayer.setVisibility(GONE);
        this.addView(relativeLayout);

        this.addView(tvInternetSpeed);
        initListener();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(context.getColor(R.color.bg_seekbar));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);


        canvas.drawCircle(0,0,0,paint);
    }

    /**
     * 事件监听接口
     */
    private void initListener() {
        ivPlayer.setImageResource(R.drawable.icon_bt_start);
        ivPlayer.setOnClickListener(this);

    }

    /**
     * 开始或者暂停 事件监听
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        //点击播放 并且渐变动画
        if (MediaPlayerManage.getInstance().getMediaPlayer()!=null&&!MediaPlayerManage.getInstance().getMediaPlayer().isPlaying()){
            infoCallBack.start();
            ivPlayer.setImageResource(R.drawable.icon_bt_pause);
            initAnimationDissapear();
        }
    }
    public void showStartIcon(){
        Log.i("执行到这了==","haode--");
        ivPlayer.setImageResource(R.drawable.icon_bt_start);
        if (MediaPlayerManage.getInstance().getMediaPlayer()!=null){
            if (this.getVisibility()==GONE) this.setVisibility(VISIBLE);
            if (ivPlayer.getVisibility()==GONE) ivPlayer.setVisibility(VISIBLE);
            if (progressBar.getVisibility()==VISIBLE) progressBar.setVisibility(GONE);
            if (tvInternetSpeed.getVisibility()==VISIBLE) tvInternetSpeed.setVisibility(GONE);
            if (tvShowPercentage.getVisibility()==VISIBLE) tvShowPercentage.setVisibility(GONE);
        }
    }
    private void initAnimationDissapear() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivPlayer,"alpha",1.0f,0.0f);
        objectAnimator.setDuration(1000);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                infoCallBack.playingState();
                ivPlayer.setVisibility(GONE);
                ivPlayer.setAlpha(1.0f);
            }
        });


    }

    /**
     * 初始化 用于视频加载前的显示
     */
    public void initView(){
        if (this.getVisibility()==GONE) {this.setVisibility(VISIBLE);}
        if (ivPlayer.getVisibility()==VISIBLE) ivPlayer.setVisibility(GONE);
        if (tvShowPercentage.getVisibility()==GONE) tvShowPercentage.setVisibility(VISIBLE);
        if (progressBar.getVisibility()==GONE) progressBar.setVisibility(VISIBLE);
        if (tvInternetSpeed.getVisibility()==GONE) tvInternetSpeed.setVisibility(VISIBLE);

    }
    public void setMediaPlayer(){

        if (MediaPlayerManage.getInstance().getMediaPlayer()!=null){
            videoLength = MediaPlayerManage.getInstance().getMediaPlayer().getDuration();
            MediaPlayerManage.getInstance().getMediaPlayer().setOnBufferingUpdateListener(this);
            tvShowPercentage.setText("0%");
            ivPlayer.setImageResource(R.drawable.icon_bt_start);
            startTime();
        }
    }

    public void unSetMediaPlayer(){

        if (MediaPlayerManage.getInstance().getMediaPlayer()!=null){
            MediaPlayerManage.getInstance().getMediaPlayer().setOnBufferingUpdateListener(null);
          if (timer!=null) timer.cancel();

        }
    }
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    //时间任务
    private  void startTime(){
        if (timer!=null) timer.cancel();
        timer = new Timer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                task = new TimerTask(){

                    @Override
                    public void run() {
                        showSpeed();
                    }
                };
                lastTotalRxBytes = InternetUtils.getInternetSpeed(context);
                lastTimeStamp = System.currentTimeMillis();
                timer.schedule(task,1000,1000);
            }
        }).start();

    }

    /**
     * 显示网速
     */
    private void showSpeed() {
        long nowTotalRxBytes = InternetUtils.getInternetSpeed(context);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        Message msg = handler.obtainMessage();
        msg.what = 100;

        if (speed<1){
            msg.obj = String.valueOf(speed*1024)+"B/s";
        }else if (speed>=1024){
            msg.obj = String.valueOf((float) speed/1024).substring(0,3)+"Mb/s";
        }else {
            msg.obj = String.valueOf(speed) + " kb/s";
        }

        handler.sendMessage(msg);//更新界面
    }


    /**
     * 实时更新当前播放位置状态
     */
    private Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String  speed = (String) msg.obj;
            tvInternetSpeed.setText(speed);
            Log.i(TAG,"网速==="+speed);
        }
    };
    private int mediaplayerPercentage;
    /**
     *
     * @param mediaPlayer
     * @param i 视频缓冲百分比
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        mediaplayerPercentage = i;
        setLoadingValue = (float) (videoLength * 0.07);//设置充盈区百分比为百分之7
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaplayerLoading = (videoLength * i) / 100;
        y = mediaplayerLoading - currentPosition;
        if (y < setLoadingValue) {//如果差值小于 设定的缓存值 则是缓冲状态
            //当 mediaplayer的缓冲百分比为100%时就不用考虑自定义缓冲进度，直接隐藏直接显示按钮
            if (i==100){
                if (this.getVisibility()==GONE) {this.setVisibility(VISIBLE);}
                if (ivPlayer.getVisibility()==GONE) ivPlayer.setVisibility(VISIBLE);
                if (tvShowPercentage.getVisibility()==VISIBLE) tvShowPercentage.setVisibility(GONE);
                if (progressBar.getVisibility()==VISIBLE) progressBar.setVisibility(GONE);
                if (tvInternetSpeed.getVisibility()==VISIBLE) tvInternetSpeed.setVisibility(GONE);
            }else {
                initLoading();

            }
        } else if (y>=setLoadingValue&&PlayerContent.PLAY_STATE==1){

            initPause();

        }else if (PlayerContent.PLAY_STATE==0){
            initPlaying();
        }

//        if (i==100&&PlayerContent.PLAY_STATE==0){
//            infoCallBack.playingState();
//            playState = PLAYING_STATE;
//            if (this.getVisibility()==VISIBLE) this.setVisibility(GONE);
//        }else if (i==100&&PlayerContent.PLAY_STATE==1){
//            infoCallBack.pauseState();
//            playState =PAUSE_STATE;
//            if (ivPlayer.getVisibility()==GONE) ivPlayer.setVisibility(VISIBLE);
//            ivPlayer.setImageResource(R.drawable.icon_bt_start);
//            if (tvShowPercentage.getVisibility()==VISIBLE) tvShowPercentage.setVisibility(GONE);
//            if (progressBar.getVisibility()==VISIBLE) progressBar.setVisibility(GONE);
//        }
        float v =  (currentPosition / videoLength);
        percentageOfLoading = (int) ((100*y)/setLoadingValue);

        if (percentageOfLoading>100){
            percentageOfLoading=100;
        }else if (percentageOfLoading<0){
            percentageOfLoading = 0;
        }

        tvShowPercentage.setText(getLoadingPercentage()+"%");
//        Log.i(TAG,"已经缓冲的大小=="+i+"\n"+"视频播放的百分比=="+v+"\n"+"设置的缓冲百分比=="+7);
        initShowOrHind();
    }
    //运行状态
    private void initPlaying() {
        playerType = PLAYING_STATE;
        infoCallBack.playingState();
        if (this.getVisibility()==VISIBLE) this.setVisibility(GONE);
    }


    //缓冲状态
    private void initLoading() {
        playerType = LOADING_STATE;
        if (this.getVisibility()==GONE) {this.setVisibility(VISIBLE);}
        if (ivPlayer.getVisibility()==VISIBLE) ivPlayer.setVisibility(GONE);
        if (tvShowPercentage.getVisibility()==GONE) tvShowPercentage.setVisibility(VISIBLE);
        if (progressBar.getVisibility()==GONE) progressBar.setVisibility(VISIBLE);
        if (tvInternetSpeed.getVisibility()==GONE) tvInternetSpeed.setVisibility(VISIBLE);
        infoCallBack.loadingState();
    }
    //暂停状态
    private void initPause( ){
        playerType = PAUSE_STATE;
        if (this.getVisibility()== VISIBLE) this.setVisibility(GONE);
//        if (ivPlayer.getVisibility()==GONE) ivPlayer.setVisibility(VISIBLE);
        ivPlayer.setImageResource(R.drawable.icon_bt_start);
        if (tvShowPercentage.getVisibility()==VISIBLE) tvShowPercentage.setVisibility(GONE);
        if (progressBar.getVisibility()==VISIBLE) progressBar.setVisibility(GONE);
        if (tvInternetSpeed.getVisibility()==VISIBLE) tvInternetSpeed.setVisibility(GONE);
        infoCallBack.pauseState();

    }

    private void initShowOrHind() {}


    /**
     * 获取 缓冲时候显示的百分比
     * @return
     */
    public int getLoadingPercentage(){
        return percentageOfLoading;
    }

    /**
     * 获取 视频缓冲的 百分比
     * @return
     */
    public int getMediaPlayerPercentage(){

        return mediaplayerPercentage;
    }

    public ImageView getIvPlayer() {
        return ivPlayer;
    }
}
