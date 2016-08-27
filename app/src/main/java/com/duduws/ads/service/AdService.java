package com.duduws.ads.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.model.AppTaskTimer;
import com.duduws.ads.receive.AdReceive;
import com.duduws.ads.utils.FuncUtils;

/**
 * Created by Pengz on 16/7/20.
 */
public class AdService extends Service{
    private static final String TAG = "AdService";

    @Override
    public void onCreate() {
        super.onCreate();

        AdReceive adReceive = new AdReceive();
        //监听时钟信息
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(adReceive, intentFilter);

        //启动APP打开和关闭的监听
        AppTaskTimer.getInstance(getApplicationContext()).startAppCheck();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //发送重启服务的广播
        Intent intent = new Intent();
        intent.setAction(ConstDefine.ACTION_RESTART_SERVER);
        sendBroadcast(intent);
        //销毁APP检测
        AppTaskTimer.getInstance(this).destroyAppCheck();
        super.onDestroy();
    }
}
