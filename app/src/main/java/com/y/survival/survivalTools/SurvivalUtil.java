package com.y.survival.survivalTools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

public class SurvivalUtil {
    private static final String TAG = SurvivalUtil.class.getSimpleName();
    private SurvivalReceiver mSurvivalReceiver;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private Intent mService1;
    private Intent mService2;
    private Intent mSurvivalAudioService;
    private boolean isOpenReceiver = false;


    protected SurvivalUtil() {
        SurvivalData.I().survivalUtil = this;

    }

    protected boolean checkData() {
        if (TextUtils.isEmpty(SurvivalData.I().getTitle())) {
            if (SurvivalData.I().survivalListener != null) {
                SurvivalData.I().survivalListener.onError("标题为空");
            }
            return false;

        }
        if (TextUtils.isEmpty(SurvivalData.I().getContent())) {
            if (SurvivalData.I().survivalListener != null) {
                SurvivalData.I().survivalListener.onError("内容为空");
            }
            return false;

        }
        if (SurvivalData.I().getIcon() == 0) {
            if (SurvivalData.I().survivalListener != null) {
                SurvivalData.I().survivalListener.onError("图标为空");
            }
            return false;

        }
        return true;

    }

    /**
     * 请求电量白名单
     */
    protected void postWhitelist() {
        PowerManager powerManager = (PowerManager) SurvivalData.I().context.getSystemService(Context.POWER_SERVICE);
        boolean isIgnoring = powerManager.isIgnoringBatteryOptimizations(SurvivalData.I().context.getPackageName());
        if (!isIgnoring) {
            Log.e(TAG, "postWhitelist:电量优化");
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + SurvivalData.I().context.getPackageName()));
            SurvivalData.I().context.startActivity(intent);
        }
        Log.e(TAG, "postWhitelist:已经打开");

    }

    protected boolean getWhitelistState() {
        PowerManager powerManager = (PowerManager) SurvivalData.I().context.getSystemService(Context.POWER_SERVICE);
        boolean isIgnoring = powerManager.isIgnoringBatteryOptimizations(SurvivalData.I().context.getPackageName());
        return isIgnoring;
    }

    /**
     * 请求后台网络数据
     */
    protected void postBackstageData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) SurvivalData.I().context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int state = connectivityManager.getRestrictBackgroundStatus();
            Log.e(TAG, "post1: state=" + state);
            switch (state) {
                case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED:
                    //未启用限制流量
                    break;

                case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                    //数据流量白名单
                    break;

                case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED://限制后台数据
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                    intent.setData(Uri.parse("package:" + SurvivalData.I().context.getPackageName()));
                    SurvivalData.I().context.startActivity(intent);

                    break;

                default:
                    break;
            }
        }
    }

    protected boolean getBackstageData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) SurvivalData.I().context.getSystemService(Context.CONNECTIVITY_SERVICE);
            int state = connectivityManager.getRestrictBackgroundStatus();
            if (state == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 启动存活服务
     */
    protected void startService1() {
        mService1 = new Intent(SurvivalData.I().context, SurvivalOneService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SurvivalData.I().context.startForegroundService(mService1);
        } else {
            SurvivalData.I().context.startService(mService1);
        }

    }

    protected void startService2() {
        mService2 = new Intent(SurvivalData.I().context, SurvivalTwoService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SurvivalData.I().context.startForegroundService(mService2);
        } else {
            SurvivalData.I().context.startService(mService2);
        }
    }

    /**
     * 启动存活定时器
     */
    protected void startAlarm() {
        if (mPendingIntent != null && mAlarmManager != null) {
            mAlarmManager.cancel(mPendingIntent);
        }
        mService1 = new Intent(SurvivalData.I().context, SurvivalOneService.class);
        long triggerTime = (System.currentTimeMillis() + 30 * 1000);
        mAlarmManager = (AlarmManager) SurvivalData.I().context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(SurvivalData.I().context, UUID.randomUUID().hashCode(), mService1, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, SurvivalData.I().getAlarmRepeatingTime(), mPendingIntent);

    }

    /**
     * 启动广播监听
     */
    protected void startReceiver() {
        if (isOpenReceiver) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        int state = SurvivalData.I().state;
        if ((state & (1 << 3)) == (1 << 3)) { //添加电量广播监听
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        }
        if ((state & (1 << 4)) == (1 << 4) || (state & (1 << 5)) == (1 << 5)) { //添加屏幕状态广播监听
            Log.e(Config.TAG, "startReceiver: 触发 添加屏幕状态广播监听");
            if (SurvivalData.I().getAudioSurvivalState()) {
                intentFilter.addAction(Intent.ACTION_SCREEN_ON);
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

            }
        }
        if (mSurvivalReceiver == null) {
            mSurvivalReceiver = new SurvivalReceiver();
        }
        SurvivalData.I().context.registerReceiver(mSurvivalReceiver, intentFilter);
        isOpenReceiver = true;
    }

    /**
     * 启动音频保活
     */
    protected void startAudioService() {
        mSurvivalAudioService = new Intent(SurvivalData.I().context, SurvivalAudioService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SurvivalData.I().context.startForegroundService(mSurvivalAudioService);
        } else {
            SurvivalData.I().context.startService(mSurvivalAudioService);
        }
    }

    /**
     * 停止音频保活
     */
    protected void stopAudioService() {
        if (mSurvivalAudioService != null) {
            SurvivalData.I().context.stopService(mSurvivalAudioService);
        }

    }

    protected void start() {
        if (!checkData()) {
            return;
        }
        int state = SurvivalData.I().state;
        if ((state & (1 << 1)) == (1 << 1)) {
            Log.e(TAG, "start: 触发");
            startService1();
            startService2();

        }
        if ((state & (1 << 2)) == (1 << 2)) {
            startAlarm();

        }
        startReceiver();
        if ((state & (1 << 4)) == (1 << 4)) {
            startAudioService();

        }
        if (SurvivalData.I().survivalListener != null) {
            SurvivalData.I().survivalListener.onStart();
        }
    }


    protected void stop() {
        stopAudioService();
        if (mService1 != null) {
            SurvivalData.I().context.stopService(mService1);

        }
        if (mService2 != null) {
            SurvivalData.I().context.stopService(mService2);
        }
        if (mAlarmManager != null) {
            mAlarmManager.cancel(mPendingIntent);
            mAlarmManager = null;
        }
        if (mSurvivalReceiver != null) {
            SurvivalData.I().context.unregisterReceiver(mSurvivalReceiver);
        }
        if (SurvivalData.I().survivalListener != null) {
            SurvivalData.I().survivalListener.onStop();
        }
        isOpenReceiver = false;


    }

    protected void destroy() {
        if (mService1 != null) {
            SurvivalData.I().context.stopService(mService1);
            mService1 = null;
        }
        if (mService2 != null) {
            SurvivalData.I().context.stopService(mService2);
            mService2 = null;
        }
        if (mAlarmManager != null) {
            mAlarmManager.cancel(mPendingIntent);
            mAlarmManager = null;
        }
        if (mSurvivalReceiver != null) {
            SurvivalData.I().context.unregisterReceiver(mSurvivalReceiver);
            mSurvivalReceiver = null;
        }
        if (SurvivalData.I().survivalListener != null) {
            SurvivalData.I().survivalListener.onDestroy();
        }

        SurvivalData.I().reset();
        isOpenReceiver = false;

    }
}
