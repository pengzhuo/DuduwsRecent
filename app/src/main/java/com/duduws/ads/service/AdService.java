package com.duduws.ads.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.duduws.ads.utils.FuncUtils;

/**
 * Created by Pengz on 16/7/20.
 */
public class AdService extends Service{
    private static final String TAG = "AdService";

    @Override
    public void onCreate() {
        super.onCreate();
        //启动APP打开和关闭的监听

        //启动心跳连接定时器

        //启动网络连接定时器

        //启动
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
        boolean isServiceRunning = FuncUtils.isServiceRunning(getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(getApplicationContext(), "com.duduws.ads.service.AdService");
        }
        super.onDestroy();
    }
}
