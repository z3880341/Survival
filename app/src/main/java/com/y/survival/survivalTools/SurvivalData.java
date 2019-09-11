package com.y.survival.survivalTools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;

import java.io.Serializable;

public class SurvivalData implements Serializable {
    protected int state = 0;
    protected SurvivalUtil survivalUtil;
    protected Context context;
    protected SurvivalListener survivalListener;
    private static SurvivalData survivalData;
    private SharedPreferences.Editor mSave;
    private SharedPreferences mGet;

    private SurvivalData(){

    }

    protected static SurvivalData I(){
        if (survivalData == null){
            survivalData = new SurvivalData();
        }
        return survivalData;

    }

    protected void init(Context context){
        this.context = context;
        mSave = context.getSharedPreferences("SurvivalConfigData", Context.MODE_PRIVATE).edit();
        mGet = context.getSharedPreferences("SurvivalConfigData",Context.MODE_PRIVATE);

    }

    protected void setTitle(String title){
        mSave.putString("title", title);
        mSave.apply();


    }

    protected String getTitle(){
        return mGet.getString("title","");

    }

    protected void setContent(String content){
        mSave.putString("content", content);
        mSave.apply();

    }

    protected String getContent(){
        return mGet.getString("content","");
    }

    protected void setIcon(@DrawableRes int resId){
        mSave.putInt("icon", resId);
        mSave.apply();

    }

    protected int getIcon(){
        return mGet.getInt("icon", 0);
    }


    protected void setAlarmRepeatingTime(long time){
        mSave.putLong("AlarmRepeatingTime", time);
        mSave.apply();

    }

    protected long getAlarmRepeatingTime(){
        return mGet.getLong("AlarmRepeatingTime", 0);
    }

    protected void setContinuousStart(boolean continuousStart){
        mSave.putBoolean("ContinuousStart", continuousStart);
        mSave.apply();

    }

    protected boolean getContinuousStart(){
        return mGet.getBoolean("ContinuousStart", false);
    }

    protected void setAudioSurvivalState(boolean automatic){
        mSave.putBoolean("AudioSurvivalState", automatic);
        mSave.apply();

    }

    protected boolean getAudioSurvivalState(){
        return mGet.getBoolean("AudioSurvivalState", false);
    }

    protected void reset(){
        SurvivalData.I().setTitle("");
        SurvivalData.I().setContent("");
        SurvivalData.I().setIcon(0);
        SurvivalData.I().setContinuousStart(false);
        SurvivalData.I().setAlarmRepeatingTime(60*1000);
        SurvivalData.I().setAudioSurvivalState(false);
        SurvivalData.I().survivalListener = null;
        SurvivalData.I().state = 0;
        SurvivalData.I().context = null;
        mSave = null;
        mGet = null;
        survivalData = null;
        survivalUtil = null;
    }


}
