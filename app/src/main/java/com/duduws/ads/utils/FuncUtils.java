package com.duduws.ads.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.util.List;

/**
 * 工具函数类
 * Created by Pengz on 16/7/20.
 */
public class FuncUtils {
    private static final String TAG = "FuncUtils";

    public static boolean isServiceRunning(Context context, String serviceName){
        boolean isRunning = false;

        if (TextUtils.isEmpty(serviceName)){
            return isRunning;
        }

        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo service : runningServiceInfos){
            if (service.service.getClassName().equalsIgnoreCase(serviceName)){
                if (service.uid == context.getApplicationInfo().uid){
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }

    public static void startDaemon(final Context context, final String clsName) {

        String executable = "libhelper.so";
        String aliasfile = "helper";
        NativeRuntime.getInstance().RunExecutable(context.getPackageName(), executable, aliasfile, context.getPackageName() + "/" + clsName);

        (new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    NativeRuntime.getInstance().startService(context.getPackageName() + "/" + clsName, createRootPath(context));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();

    }

    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String createRootPath(Context context) {
        String rootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            rootPath = context.getExternalCacheDir()
                    .getPath();
        } else {
            // /data/data/<application package>/cache
            rootPath = context.getCacheDir().getPath();
        }
        return rootPath;
    }
}
