package com.y.survival;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.y.survival.survivalTools.Survival;
import com.y.survival.survivalTools.SurvivalListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mBtnStart
            , mBtnStop;
    private Survival.Build mBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStop = findViewById(R.id.btn_stop);
        initClick();
        initSurvival();
    }

    private void initClick(){
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuild.start();

            }
        });
        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuild.stop();

            }
        });
    }

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
}
