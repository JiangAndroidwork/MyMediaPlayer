# MyMediaPlayer
视频播放
## 特点：可以播放本地视频和网络视频
### 1，添加依赖：
### 在工程的build.gradle文件中添加:
```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
### 在moudle 中build.gradle中添加依赖：
```
3r3
```
### 2，调用方法：
#### （1） 在xml中使用控件 com.laojiang.lib_mediaplay.MediaPlayerView
#### （2） 使用代码 new MediaPlayerView(context);

```
 List<VideoResourceBean> list = new ArrayList<>();
        list.add(new VideoResourceBean("http://114.215.83.40/cloudfile/public/microclass/files201707121530004016/哈哈_20170712153000150.mp4"));
        list.add(new VideoResourceBean("http://114.215.83.40/cloudfile/public/microclass/files201707130846575996/苏州园林_20170713084657860.mp4"));
        mediaPlayerView.setUrl(list);
```
##说明：
VideoResourceBean是媒体基类，用于存放路径
#### 监听事件：
##### 1,网络状态监听
```
 mediaPlayerView.setNetWorkInfoListener(this);

@Override
    public void callBackInternetInfo(String msg, int code) {
        Log.i("接收到的网络状态==",msg+"---"+code);
    //public static final int MOBILE_NET= 111;//只有移动数据
    //public static final int WIFI_NET = 222;//只有wifi
    //public static final int MOBILE_WIFI_NET = 333;//都有
    //public static final int ALL_NO_NET = 444;//都没有
    }
```
##### 2，错误监听
```
 mediaPlayerView.setErrorListener(new PlayerErrorCallBack(){
            @Override
            public void playErrorInfo(String url, String message) {
                Toast.makeText(MainActivity.this,url+"\n"+message,Toast.LENGTH_SHORT).show();
            }
        });
 ```
#### 横竖屏
```
  @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //变成横屏了
            hindSystemUI();//隐藏状态栏和虚拟按键
            mediaPlayerView.setVideoParams(mediaPlayerView.getMediaPlayer(), true);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //变成竖屏了
            hindSystemUI();//隐藏状态栏和虚拟按键
            mediaPlayerView.setVideoParams(mediaPlayerView.getMediaPlayer(), false);
        }
    }
```
#### 生命周期监听
```
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
```
