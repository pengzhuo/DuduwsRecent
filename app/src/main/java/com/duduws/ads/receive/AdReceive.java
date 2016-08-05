package com.duduws.ads.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.model.AppTaskTimer;
import com.duduws.ads.net.NetManager;
import com.duduws.ads.service.AdService;
import com.duduws.ads.utils.DspHelper;
import com.duduws.ads.utils.FuncUtils;

/**
 * 广播监听
 * Created by Pengz on 16/7/29.
 */
public class AdReceive extends BroadcastReceiver{
    private static final String TAG = "AdReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MLog.i(TAG, "AdReceive action " + action);
        if (TextUtils.isEmpty(action)){
            return;
        }
        if (action.equalsIgnoreCase(Intent.ACTION_USER_PRESENT)) {
            //用户解锁进入桌面
            handleLockScreen(context);
        } else if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //网络发生变化
            handleRestartService(context, ConstDefine.SERVICE_RESTART_NET);
            handleNetworkChange(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            //设备重启完成
            handleBootCompleted(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_TIME_TICK)) {
            //时钟信息
            handleTimeTick(context);
        } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
            //解锁
            AppTaskTimer.getInstance(context).startAppCheck();
        } else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
            //锁屏
            AppTaskTimer.getInstance(context).pauseAppCheck();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_NETWORK)) {
            //连接服务器
            NetManager.getInstance(context).startRequest();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_HEART)) {
            //发送心跳
            NetManager.getInstance(context).startHeart();
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_ALARM_RECENT_APP)) {
            //更新RecentApp
            FuncUtils.updateRecentApp(context);
        } else if (action.equalsIgnoreCase(ConstDefine.ACTION_RESTART_SERVER)) {
            //重启服务
            handleRestartService(context, ConstDefine.SERVICE_RESTART_SELF);
        }
    }

    /**
     * 处理解锁事件
     * @param context
     */
    public void handleLockScreen(Context context) {
        long time = DspHelper.getDspSpotLastTime(context);
        if (!DateUtils.isToday(time)){
            //重置数据
            DspHelper.resetData(context);
        }

        int channel = DspHelper.getDspSpotLockChannel(context);
        if (channel != ConstDefine.DSP_GLOABL) {
            if (FuncUtils.hasActiveNetwork(context)) {
                //展示广告
                DspHelper.showAds(context, channel, ConstDefine.TRIGGER_TYPE_UNLOCK);
            }
        }
    }

    /**
     * 处理网络变化
     * @param context
     */
    public void handleNetworkChange(Context context) {
        long time = DspHelper.getDspSpotLastTime(context);
        if (!DateUtils.isToday(time)){
            //重置数据
            DspHelper.resetData(context);
        }

        int channel = DspHelper.getDspSpotNetworkChannel(context);
        if (channel != ConstDefine.DSP_GLOABL) {
            if (FuncUtils.hasActiveNetwork(context)) {
                //展示广告
                DspHelper.showAds(context, channel, ConstDefine.TRIGGER_TYPE_NETWORK);
            }
        }
    }

    /**
     * 处理设备重启完成
     * @param context
     */
    public void handleBootCompleted(Context context) {
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(context.getApplicationContext(), "com.duduws.ads.service.AdService");
        }
    }

    /**
     * 处理时钟信息
     * @param context
     */
    public void handleTimeTick(Context context) {
        //守护进程
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(context.getApplicationContext(), "com.duduws.ads.service.AdService");
        }
        //Adservice服务
        handleRestartService(context, ConstDefine.SERVICE_RESTART_OTHER);
        //是否需要连接服务器
        long curTime = System.currentTimeMillis();
        long nextTime = DspHelper.getNextNetConTime(context);
        MLog.i(TAG, "$$$$$$$$$$$$$$$$ net con " + curTime + " , " + nextTime);
        if (nextTime == 0 || curTime >= nextTime){
            Intent intent = new Intent();
            intent.setAction(ConstDefine.ACTION_ALARM_NETWORK);
            context.sendBroadcast(intent);
            DspHelper.setNextNetConTime(context, System.currentTimeMillis() + DspHelper.getNetConTime(context));
        }
        //是否需要发送心跳
        nextTime = DspHelper.getNextNetHeartTime(context);
        MLog.i(TAG, "$$$$$$$$$$$$$$$$ heart con " + curTime + " , " + nextTime);
        if (nextTime == 0 || curTime >= nextTime){
            Intent intent = new Intent();
            intent.setAction(ConstDefine.ACTION_ALARM_HEART);
            context.sendBroadcast(intent);
            DspHelper.setNextHeartConTime(context, System.currentTimeMillis() + DspHelper.getHeartConTime(context));
        }
    }

    /**
     * 处理重启服务
     * @param context
     */
    public void handleRestartService(Context context, int type) {
        boolean isServiceRunning = FuncUtils.isServiceRunning(context.getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            Intent intent = new Intent(context, AdService.class);
            intent.putExtra("type", type);
            context.startService(intent);
        }
    }

}
