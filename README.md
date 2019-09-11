# Survival
一个Android端保活集成框架


如果你需要将工具加入到其他项目中,请注意需要在清单文件AndroidManifest.xml 里注册三个服务

<service android:name=".survivalTools.SurvivalAudioService"/>
<service android:name=".survivalTools.SurvivalOneService"/>
<service android:name=".survivalTools.SurvivalTwoService"/>



使用方法简介
private void initSurvival(){
        mBuild = new Survival(MainActivity.this, "测试","测试",R.mipmap.ic_launcher)
                .setDoubleServiceSurvival(true)//双服务互启保活
                .setServiceContinuousStart(false)//取消双服务保活,每次触发都启动服务
                .setAlarmRepeatingTime(60*1000L)//定时器保活
                .setReceiver(true)//广播保活
                .setAudioSurvival(true)
                .setListener(new SurvivalListener() {
                    @Override
                    public void onStart() {
                        Log.e(TAG, "onStart: Survival 触发");

                    }

                    @Override
                    public void onHeartbeat(int heartbeatId) {
                        Log.e(TAG, "onHeartbeat: Survival触发心跳heartbeatId="+heartbeatId);
                        //此方法为工具内置的心跳回调,工具有2个服务所以会有2个心跳id,各种每隔20秒跳动一次

                    }

                    @Override
                    public void onStop() {
                        Log.e(TAG, "onStop: Survival");

                    }

                    @Override
                    public void onDestroy() {
                        Log.e(TAG, "onDestroy: Survival");

                    }

                    @Override
                    public void onError(String reason) {
                        Log.e(TAG, "onError: Survival异常="+reason);

                    }
                })
                .build();
    }
