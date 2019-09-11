package com.y.survival.survivalTools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SurvivalOneService extends Service {
    private static final String CHANNEL_ID = "SurvivalService";
    private Handler mHandler;
    private boolean notificationVisibility = false;
    protected static long sSurvivalOneTime = 0;

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
        Log.e(Config.TAG, "onStartCommand: 服务启动＿1");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && !notificationVisibility) {
            highVersionNotification();
        }
        initHandler();
        sSurvivalOneTime = System.currentTimeMillis();
        return super.onStartCommand(intent, flags, startId);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
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

    private void initHandler() {
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (SurvivalData.I().survivalUtil != null) {
                            if (SurvivalData.I().getContinuousStart()) { //连续启动模式
                                SurvivalData.I().survivalUtil.startService1();
                            } else if ((System.currentTimeMillis() - SurvivalTwoService.sSurvivalTwoTime) > 60 * 1000) {
                                SurvivalData.I().survivalUtil.startService1();
                            }
                        }
                        mHandler.sendEmptyMessageDelayed(1, 30 * 1000);
                        if (SurvivalData.I().survivalListener != null) {
                            SurvivalData.I().survivalListener.onHeartbeat(1);
                        }
                        sSurvivalOneTime = System.currentTimeMillis();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mHandler.sendEmptyMessageDelayed(1, 10 * 1000);
    }

}
