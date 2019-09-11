package com.y.survival.survivalTools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SurvivalReceiver extends BroadcastReceiver {

    protected SurvivalReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == Intent.ACTION_BATTERY_CHANGED){
            SurvivalData.I().survivalUtil.startService1();
            SurvivalData.I().survivalUtil.startService2();
        }
        int state = SurvivalData.I().state;
        Log.e(Config.TAG, "onReceive: 广播触发="+intent.getAction());
        if ((state & (1 << 4)) == (1 << 4)){
            if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {//屏幕关闭
                Log.e(Config.TAG, "onReceive: 触发 - 音频保活 - 屏幕关闭");
                SurvivalData.I().survivalUtil.startAudioService();

            }
            if (intent.getAction() == Intent.ACTION_SCREEN_ON) {//屏幕开启
                Log.e(Config.TAG, "onReceive: 触发 - 音频保活 - 屏幕开启");
                SurvivalData.I().survivalUtil.stopAudioService();

            }
        }
    }
}
