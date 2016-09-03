package com.duduws.ads.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.utils.FuncUtils;

/**
 * Created by Pengz on 16/7/20.
 */
public class MainService extends Service {
    private static final String TAG = "MainService";

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isServiceRunning = FuncUtils.isServiceRunning(getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(getApplicationContext(), ConstDefine.ACTION_MAIN_SERVICE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        boolean isServiceRunning = FuncUtils.isServiceRunning(getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            //启动服务
            FuncUtils.startDaemon(getApplicationContext(), ConstDefine.ACTION_MAIN_SERVICE);
        }
        super.onDestroy();
    }
}
