package com.laojiang.lib_mediaplay;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.laojiang.lib_mediaplay.content.PlayerContent;
import com.laojiang.lib_mediaplay.listener.PlayerControlListener;
import com.laojiang.lib_mediaplay.listener.ScreenSwitchListener;
import com.laojiang.lib_mediaplay.weight.PlayerWindowInfoView;

/**
 * 类介绍（必填）：播放视频底部控制view
 * Created by Jiang on 2017/7/19 8:35.
 */

public class MediaControllerView extends RelativeLayout implements View.OnClickListener {

    private View layoutView;
    private SeekBar seekBar;
    private TextView tvTime;
    private ImageView btScreen;
    private ImageView btPlayerState;//播放按钮状态
    private PlayerControlListener controlListener;//播放状态接口
    private ScreenSwitchListener switchListener;//屏幕切换接口
    private ImageView btNext;
    private VideoResourceBean bean;
    private PlayerWindowInfoView windowInfoView;
    private boolean isButtonSP = false;

    public MediaControllerView(Context context) {
        super(context);
        init(context);
    }

    public MediaControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        layoutView = LayoutInflater.from(context).inflate(R.layout.layout_media_control, null);

        btPlayerState = layoutView.findViewById(R.id.bt_player_state);
        seekBar = layoutView.findViewById(R.id.seek_bar);
        tvTime = layoutView.findViewById(R.id.tv_time);
        btScreen = layoutView.findViewById(R.id.bt_screen);
        btNext = layoutView.findViewById(R.id.bt_next);
        btScreen.setOnClickListener(this);
        btNext.setOnClickListener(this);
        this.setBackgroundColor(R.color.bg_controller);
        setPlayerStateListener();
        this.addView(layoutView);
        initView();
    }

    private void initView() {

    }

    public void setInfoViewListener(PlayerWindowInfoView windowInfoView){
        this.windowInfoView = windowInfoView;
    }
    /**
     * 设置开始暂停接口
     * @param listener
     */
    public void setStateListener(PlayerControlListener listener) {
        this.controlListener = listener;
    }

    public void setScreenListener(ScreenSwitchListener listener) {
        this.switchListener = listener;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public TextView getTvTime() {
        return tvTime;
    }

    public ImageView getBtScreen() {
        return btScreen;
    }

    public ImageView getBtPlayerState() {
        return btPlayerState;
    }

    public void setAllEnablle_True() {
        seekBar.setEnabled(true);
        btPlayerState.setEnabled(true);
        btNext.setEnabled(true);
        if (tvTime.getVisibility()==GONE) {
            tvTime.setVisibility(VISIBLE);
        }
    }

    public void setAllEnable_False() {
        seekBar.setEnabled(false);
        btPlayerState.setEnabled(false);
        btNext.setEnabled(false);
        if (tvTime.getVisibility()==VISIBLE) {
            tvTime.setVisibility(GONE);
        }
    }

    public void setPlayerStateListener() {
        btPlayerState.setOnClickListener(this);
    }

    /**
     * 播放状态接口
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        Log.i("播放按钮点击事件===", "执行了");
        if (view.getId() == R.id.bt_player_state) {


            if (isButtonSP) {//默认 暂停
                initStart();
                isButtonSP = false;
            } else  {//播放状态
                initPuase();
                isButtonSP = true;
            }
        } else if (view.getId() == R.id.bt_next) {
            controlListener.next(null);
        } else {
            if (PlayerContent.SCREEN_STATE == 0) {//竖屏
                switchListener.horizontalScreen();
                PlayerContent.SCREEN_STATE = 1;
            } else {//横屏
                switchListener.verticalScreen();
                PlayerContent.SCREEN_STATE = 0;
            }
        }
    }

    private void initStart() {
        windowInfoView.setMediaPlayer();//开始监听
        controlListener.start();//开始播放
        PlayerContent.IS_BUTTON_START = true;
        btPlayerState.setImageResource(R.drawable.icon_bt_pause);
    }

    private void initPuase() {
        controlListener.pause();

        if (windowInfoView.playerType.equals(PlayerWindowInfoView.PLAYING_STATE)
                |windowInfoView.playerType.equals(PlayerWindowInfoView.LOADING_STATE)) {
            windowInfoView.showStartIcon();}
        windowInfoView.unSetMediaPlayer();//取消监听
        PlayerContent.PLAY_STATE = 1;
        PlayerContent.IS_BUTTON_START = false;
        btPlayerState.setImageResource(R.drawable.icon_bt_start);
    }

    public void setNextVideoResrouce(VideoResourceBean bean){
        this.bean = bean;

    }
}
