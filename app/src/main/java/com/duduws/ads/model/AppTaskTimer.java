package com.duduws.ads.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.AdsPreferences;
import com.duduws.ads.utils.DspHelper;
import com.duduws.ads.utils.FuncUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/4 16:57
 */
public class AppTaskTimer {
    private static final String TAG = "AppTaskTimer";
    private Context mContext;
    private static AppTaskTimer instance;
    private Timer scanAppTimer;
    private AlarmManager alarmManager;
    private static final long INTERVAL = 2000L;
    private static final long DEFAULT_REQUEST_DELAY = 1000L;
    private static final long DEFAULT_HEART_DELAY = 1000L;
    private static String oldPackageName = null;

    private AppTaskTimer(Context context) {
        mContext = context;
    }

    public static AppTaskTimer getInstance(Context context) {
        if (instance == null) {
            instance = new AppTaskTimer(context);
        }
        return instance;
    }

    /**
     * 开启APP检测
     */
    public void startAppCheck() {
        if (scanAppTimer == null) {
            scanAppTimer = new Timer();
            scanAppTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String bbList = AdsPreferences.getInstance(mContext).getString(ConstDefine.BB_LIST_STRING, "");
                    String packageName = FuncUtils.appInFront(mContext);
                    //包名为空或为自身，则直接返回
                    if (oldPackageName != null){
//                        MLog.e(TAG, packageName + ", " + oldPackageName + ", " + mContext.getPackageName());
                    }
                    if (TextUtils.isEmpty(oldPackageName) ||
                            TextUtils.isEmpty(packageName) ||
                            TextUtils.equals(oldPackageName, packageName)){
//                        MLog.e(TAG, "startAppCheck fail !");
                    }else{
                        boolean flag = false;
                        if (!TextUtils.isEmpty(oldPackageName) &&
                                !FuncUtils.isSystemApp(mContext, oldPackageName) &&
                                !bbList.contains(oldPackageName) &&
                                !oldPackageName.equalsIgnoreCase(mContext.getPackageName())){
                            //检测应用退出
                            long time = DspHelper.getDspSpotLastTime(mContext);
                            if (!DateUtils.isToday(time)){
                                //重置数据
                                DspHelper.resetData(mContext);
                            }

                            int channel = DspHelper.getDspSpotAppExitChannel(mContext);
                            if (channel != ConstDefine.DSP_GLOABL){
                                flag = true;
                                if (FuncUtils.hasActiveNetwork(mContext)){
                                    //展示广告
                                    DspHelper.showAds(mContext, channel, ConstDefine.TRIGGER_TYPE_APP_EXIT);
                                }
                            }
                        }
                        if (!flag &&
                                !FuncUtils.isSystemApp(mContext, packageName) &&
                                !bbList.contains(packageName) &&
                                !packageName.equalsIgnoreCase(mContext.getPackageName())){
                            //检测应用进入
                            long time = DspHelper.getDspSpotLastTime(mContext);
                            if (!DateUtils.isToday(time)){
                                //重置数据
                                DspHelper.resetData(mContext);
                            }

                            int channel = DspHelper.getDspSpotAppEnterChannel(mContext);
                            if (channel != ConstDefine.DSP_GLOABL){
                                if (FuncUtils.hasActiveNetwork(mContext)){
                                    //展示广告
                                    DspHelper.showAds(mContext, channel, ConstDefine.TRIGGER_TYPE_APP_ENTER);
                                }
                            }
                        }
                    }
                    oldPackageName = packageName;
                }
            }, 0, INTERVAL);
        }
    }

    /**
     * 暂停APP检测
     */
    public void pauseAppCheck() {
        if (scanAppTimer != null) {
            scanAppTimer.cancel();
            scanAppTimer = null;
        }
    }

    /**
     * 销毁APP检测
     */
    public void destroyAppCheck() {
        if (scanAppTimer != null) {
            scanAppTimer.cancel();
            scanAppTimer = null;
        }
        instance = null;
        oldPackageName = null;
    }

    /**
     * 开启网络连接闹钟
     */
    public void startNetworkAlarm() {
        if (alarmManager == null){
            alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        }
        long interval = DEFAULT_REQUEST_DELAY;
        long curTime = System.currentTimeMillis();
        long nextTime = DspHelper.getNextNetConTime(mContext);
        if (nextTime > 0 && curTime < nextTime){
            interval = nextTime - curTime;
        }
        MLog.i(TAG, "startNetworkAlarm " + curTime + "," + nextTime + "," + interval);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ConstDefine.ACTION_ALARM_NETWORK), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, interval, pi);
    }

    /**
     * 开启心跳连接闹钟
     */
    public void startHeartAlarm() {
        if (alarmManager == null){
            alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        }
        long interval = DEFAULT_REQUEST_DELAY;
        long curTime = System.currentTimeMillis();
        long nextTime = DspHelper.getNextNetConTime(mContext);
        if (nextTime > 0 && curTime < nextTime){
            interval = nextTime - curTime;
        }
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ConstDefine.ACTION_ALARM_HEART), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, interval, pi);
    }

    /**
     * 开启指定ACTION的闹钟
     * @param interval
     * @param action
     */
    public void startActionAlarm(long interval, String action) {
        if (alarmManager == null){
            alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        }
        long curTime = System.currentTimeMillis();
        long nextTime = curTime + interval;
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(action), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
    }
}
