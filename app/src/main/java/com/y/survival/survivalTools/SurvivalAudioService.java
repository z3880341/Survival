package com.y.survival.survivalTools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;

import com.y.survival.R;

public class SurvivalAudioService extends Service {
    private MediaPlayer mMediaPlayer;
    private static final String CHANNEL_ID = "SurvivalService";
    private boolean notificationVisibility = false;
    private Handler mHandler;
    public SurvivalAudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!notificationVisibility){
            highVersionNotification();
        }
        initHandler();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()){ //如果音频已经正在播放,就不在重复播放
            return super.onStartCommand(intent, flags, startId);
        }
        if (SurvivalData.I().getAudioSurvivalState()){ //判断是否选择自动模式
            if (!checkScreenState()){ //灭屏状态 就开启无声音频播放
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startPlayAudio();
                    }
                }).start();
            }else { //亮屏幕就关闭
                stopPlayAudio();
            }
        }else { //如果不自动模式,启动服务就直接开启播放音频
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startPlayAudio();
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private void highVersionNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, SurvivalData.I().getTitle(), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(true);
            notificationChannel.setDescription(SurvivalData.I().getContent());
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(this)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle(SurvivalData.I().getTitle())
                    .setContentText(SurvivalData.I().getContent())
                    .setSmallIcon(SurvivalData.I().getIcon())
                    .build();

            startForeground(1, notification);
            notificationVisibility = true;
        }
    }

    private boolean checkScreenState(){
        PowerManager manager = (PowerManager) getSystemService(POWER_SERVICE);
        return manager.isInteractive();
    }

    private void startPlayAudio(){
        if (mMediaPlayer == null){
            mMediaPlayer = MediaPlayer.create(this, R.raw.no_notice);
        }
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

    }

    private void stopPlayAudio(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
        }
    }

    private void initHandler(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        mHandler.sendEmptyMessageDelayed(1,30*1000);
                        if (SurvivalData.I().survivalListener != null){
                            SurvivalData.I().survivalListener.onHeartbeat(3);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mHandler.sendEmptyMessageDelayed(1,30*1000);

    }

}
