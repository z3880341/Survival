package com.y.survival.survivalTools;

import android.content.Context;

public class Survival {

    /**
     * 设置上下文与,服务数据
     * <p>
     * 需要在AndroidManifest.xml注册  所以前需要添加下面3个服务
     * <p>
     * <p>
     * <service
     * android:name=".survivalTools.SurvivalAudioService"
     * android:enabled="true"
     * android:exported="true" />
     * <service
     * android:name=".survivalTools.SurvivalTwoService"
     * android:enabled="true"
     * android:exported="true" />
     * <service
     * android:name=".survivalTools.SurvivalOneService"
     * android:enabled="true"
     * android:exported="true" />
     *
     * </p>
     *
     * @param context 上下文
     * @param title   服务标题
     * @param content 服务内容
     * @param icon    服务图标
     */
    public Survival(Context context, String title, String content, Integer icon) {
        SurvivalData.I().init(context);
        SurvivalData.I().setTitle(title);
        SurvivalData.I().setContent(content);
        SurvivalData.I().setIcon(icon);

    }

    /**
     * 设置双服务保活
     *
     * @param isOpen
     * @return
     */
    public Survival setDoubleServiceSurvival(boolean isOpen) {
        if (isOpen) {
            SurvivalData.I().state = SurvivalData.I().state + (1 << 1);
        }
        return this;
    }

    /**
     * 设置服务连续启动,设置这个后每次心跳都会启动一次服务
     *
     * @param continuousStart true=连续 false=不连续
     * @return
     */
    public Survival setServiceContinuousStart(boolean continuousStart) {
        SurvivalData.I().setContinuousStart(continuousStart);
        return this;

    }

    /**
     * 设置重复定时器保活
     *
     * @param alarmRepeatingTime 重复时间 建议在设置等于或者超过60秒时间,过短的时间依然会被拉长时间
     * @return
     */
    public Survival setAlarmRepeatingTime(Long alarmRepeatingTime) {
        SurvivalData.I().setAlarmRepeatingTime(alarmRepeatingTime);
        SurvivalData.I().state = SurvivalData.I().state + (1 << 2);
        return this;
    }

    /**
     * 设置广播监听保活
     *
     * @param isOpen 是否打开
     * @return
     */
    public Survival setReceiver(boolean isOpen) {
        if (isOpen) {
            SurvivalData.I().state = SurvivalData.I().state + (1 << 3);
        }
        return this;
    }

    public Survival setListener(SurvivalListener listener) {
        SurvivalData.I().survivalListener = listener;
        return this;

    }

    /**
     * 设置音频播放保活
     *
     * @param automatic true=为自动模式,自动锁屏后播放,亮屏后自动关闭
     *                  false=手动模式,调用start()方法后会马上播放,调用stop()方法后会马上停止
     * @return
     */
    public Survival setAudioSurvival(boolean automatic) {
        SurvivalData.I().setAudioSurvivalState(automatic);
        SurvivalData.I().state = SurvivalData.I().state + (1 << 4);
        return this;

    }
    
    public Build build() {
        return new Build();

    }

    public class Build {
        private SurvivalUtil survivalUtil;

        protected Build() {
            survivalUtil = new SurvivalUtil();
        }

        /**
         * 申请电量白名单
         * 所需注册权限   <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
         */
        public void applyBatteryWhitelist() {
            survivalUtil.postWhitelist();

        }

        public boolean getBatteryWhitelistState() {
            return survivalUtil.getWhitelistState();

        }

        /**
         * 申请后台网络白名单
         * 所需注册权限   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
         */
        public void applyBackstageWhitelist() {
            survivalUtil.postBackstageData();

        }

        public boolean getBackstageWhitelistState() {
            return survivalUtil.getBackstageData();

        }


        public void start() {
            survivalUtil.start();

        }

        public void stop() {
            survivalUtil.stop();

        }

        public void destroy() {
            survivalUtil.destroy();

        }

    }
}
