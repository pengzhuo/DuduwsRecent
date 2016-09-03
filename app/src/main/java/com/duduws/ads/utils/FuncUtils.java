package com.duduws.ads.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;

import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.model.PackageElement;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 工具函数类
 * Created by Pengz on 16/7/20.
 */
public class FuncUtils {
    private static final String TAG = "FuncUtils";

    private static final String PKGNAME_BROWSER_SYS = StrUtils.deCrypt("com.android.browser");
    private static final String PREFS_FILE_NAME = StrUtils.deCrypt("recent_apps");
    private static final String PREFS_KEY_RECENT = StrUtils.deCrypt("recent");
    private static final int TOTAL_RECENT_APPS = 13;

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
            rootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            rootPath = context.getCacheDir().getPath();
        }
        return rootPath;
    }

    /**
     * dip单位转换
     */
    public static int getDip(Context context,float size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 启动指定应用
     *
     * @param context
     * @param packageName
     */
    public static void runApps(Context context, String packageName) {
        if (null == packageName || packageName.length() < 1) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 检查app是否是系统rom集成的
     * @param pname
     * @return
     */
    public static boolean isSystemApp(Context context, String pname) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(pname, 0);
            // 是系统软件或者是系统软件更新
            if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
                MLog.i(TAG, pname + " is system app !");
                return true;
            } else {
                MLog.i(TAG, pname + " is not system app !");
                return false;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public static boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    private static boolean isUserApplication(Context context, String pkgName) {
        MLog.d("isUserApplication, pkgname: " + pkgName);
        if (!TextUtils.isEmpty(pkgName)) {
            try {
                if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                    return false;
                }
                // skip SYSTEM BROWSER
                if (pkgName.equalsIgnoreCase(PKGNAME_BROWSER_SYS)) {
                    return true;
                }

                PackageInfo pInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
                if (pInfo != null) {
                    boolean isUserApp = isUserApp(pInfo);
                    MLog.d("isUserApplication, isUserApp: " + isUserApp);
                    return isUserApp;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    public static String getRecentAppString(Context context) {
        MLog.d("RecentTasksHelper.getRecentAppString begin");
//		String recentApp = updateRecentApp(context);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, 0);
        String recentApp = prefs.getString(PREFS_KEY_RECENT, "");
        MLog.d("RecentTasksHelper.getRecentAppString recentApp: " + recentApp);
        if (TextUtils.isEmpty(recentApp)) {
            MLog.d("RecentTasksHelper.getRecentAppString to Update Recent Apps.");
            recentApp = updateRecentApp(context);
            MLog.d("RecentTasksHelper.getRecentAppString updated recentApp: " + recentApp);
        }

        return recentApp;
    }

    public static void setRecentAppString(Context context, String recentApp) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_KEY_RECENT, recentApp);
        editor.commit();
    }

    public static List<String> getRecentTaskList( Context context )
    {
        MLog.d("RecentTasksHelper.getRecentTaskList begin");
        List<String> rts = new ArrayList<String>();

        try
        {
            MLog.d("RecentTasksHelper.getRecentTaskList 1");
            ActivityManager am = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );
            List<ActivityManager.RecentTaskInfo> list = am.getRecentTasks( 64, 0 );
            MLog.d("RecentTasksHelper.getRecentTaskList 2");
            PackageManager pm  = context.getPackageManager();
            String pkgName = "";
            for(ActivityManager.RecentTaskInfo task : list )
            {
                MLog.d("recent list.size: "+list.size());
                ComponentName cn = task.origActivity;
                if( cn != null )
                {
                    pkgName = cn.getPackageName();

                    MLog.d("RecentTasksHelper.getRecentTaskList cn.getPackageName: " + pkgName);
                    if (!TextUtils.isEmpty(pkgName) && isUserApplication(context, pkgName)) {
                        MLog.d("RecentTasksHelper.getRecentTaskList userapp, add it");
                        rts.add(pkgName);
                        continue;
                    }
                }

                Intent baseIntent = task.baseIntent;
                ResolveInfo ri = pm.resolveActivity( baseIntent, 0 );
                if( ri != null )
                {
                    pkgName = ri.activityInfo.packageName;
                    MLog.d("RecentTasksHelper.getRecentTaskList ri.activityInfo.packageName: " + pkgName);
                    if (!TextUtils.isEmpty(pkgName) && isUserApplication(context, pkgName)) {
                        MLog.d("RecentTasksHelper.getRecentTaskList userapp, add it 222");
                        rts.add(pkgName);
                        continue;
                    }
                }
            }
        } catch (SecurityException e)
        {
        }
        MLog.d("RecentTasksHelper.getRecentTaskList end");
        return rts;
    }

    public static String updateRecentApp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE_NAME, 0);

        String recentApp = prefs.getString(PREFS_KEY_RECENT, "");
        MLog.d("RecentTasksHelper.updateRecentApp begin, recentApp: "+recentApp);

        List<String> recentList = getRecentTaskList(context);
        if (recentList != null && recentList.size() > 0) {
            MLog.d("AdsReceiver.updateRecentApp recentList.size : "+recentList.size() + ", toString: " + recentList.toString());

            String bbList = AdsPreferences.getInstance(context).getString(ConstDefine.BB_LIST_STRING, "");
            MLog.d("AdsReceiver.updateRecentApp bbList: "+bbList);
            for (int i=0; i< recentList.size(); i++) {
                String pkgname = recentList.get(i);
                MLog.d("AdsReceiver.updateRecentApp pkgName: "+pkgname);
                if (bbList.contains(pkgname)) {
                    MLog.d("AdsReceiver.updateRecentApp bblist contains: "+pkgname);
                    if (recentApp.contains(pkgname)) {
                        MLog.d("AdsReceiver.updateRecentApp recentApp contains: "+pkgname+",recentApp: "+recentApp);
                        recentApp = recentApp.replace(pkgname, "");
                        MLog.d("AdsReceiver.updateRecentApp recentApp replaced: "+recentApp);
                    }
                    continue;
                }
                if (TextUtils.isEmpty(pkgname) || recentApp.contains(pkgname)) {
                    MLog.d("AdsReceiver.updateRecentApp pkgname empty or exists");
                    continue;
                }
                recentApp += pkgname + ", ";

            }

            MLog.d("AdsReceiver.updateRecentApp neoRecentPkgNameList: "+recentApp + ", total: " + recentApp);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_KEY_RECENT, recentApp);
            editor.commit();
        }

        MLog.d("AdsReceiver.updateRecentApp end");
        return recentApp;
    }

    public static ArrayList<PackageElement> getRecentApps(Context context)
    {
        MLog.d("RecentTasksHelper.getRecentApps begin");

        String recentApp = updateRecentApp(context);
        MLog.d("RecentTasksHelper.getRecentApps recentApp: " + recentApp);
        if (TextUtils.isEmpty(recentApp)) {
            return null;
        }

        String[] recents = recentApp.split(",");
        if (recents == null || recents.length < 1) {
            return null;
        }

        MLog.d("RecentTasksHelper.getRecentApps recentApp: " + recents.toString());

        int total = 0;
        PackageManager pm = context.getPackageManager();
        ArrayList<PackageElement> rts = new ArrayList<PackageElement>();
        for (int i=recents.length-1; i>=0; i--) {
            String recent = recents[i];
            if (TextUtils.isEmpty(recent)) {
                continue;
            }

            if (total >= TOTAL_RECENT_APPS) {
                break;
            }

            String pkgname = recent.trim();

            try {
                PackageElement pe = new PackageElement();
                ApplicationInfo info = pm.getApplicationInfo(pkgname, 0);
                String name = pm.getApplicationInfo(pkgname, 0).loadLabel(pm).toString();
                pe.setLabel(name);
                pe.setmIcon(info.loadIcon(pm));
                pe.setmPackageName(pkgname);
                pe.setmIsNative(false);
                rts.add(pe);
                total++;
            } catch (Exception e) {

            }
        }

        MLog.d("RecentTasksHelper.getRecentTaskList end");
        return rts;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean hasActiveNetwork(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 检查网络连接，如果无网络可用，就不需要进行连网操作等
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取顶层APP
     * @param context
     * @return
     */
    public static String appInFront(Context context){
        String str = "";
        if (Build.VERSION.SDK_INT >= 21) {
            Iterator localIterator = AndroidProcesses.getRunningForegroundApps(context).iterator();

            while (localIterator.hasNext())
            {
                AndroidAppProcess localAndroidAppProcess = (AndroidAppProcess)localIterator.next();
                str = localAndroidAppProcess.getPackageName();
                if (TextUtils.isEmpty(str))
                {
                    if (localAndroidAppProcess.foreground) {
                        return str;
                    }
                }
            }
            return str;
        }else{
            ActivityManager mActivityManager  = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rtList = mActivityManager.getRunningTasks(1);
            if( rtList == null ||
                    rtList.size() == 0 ){
                return "" ;
            }
            ActivityManager.RunningTaskInfo taskInfo = rtList.get(0);
            if (null != taskInfo) {
                String packageName = taskInfo.topActivity.getPackageName();
                return packageName;
            }
            return "";
        }
    }

    /**
     * 得到手机的IMEI号，需要context参数,获取不到时，返回“000000000000000”
     */
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            if (context != null) {
                TelephonyManager mTelephonyMgr = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                imei = mTelephonyMgr.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return TextUtils.isEmpty(imei) ? getLocalMacAddress(context) : imei;
    }

    public static String getLocalMacAddress(Context context) {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            info.getMacAddress().replace(":", "");
        } catch (Exception e) {
            mac = "";
        }

        return mac;
    }

    public static String getManifestApplicationMetaData(Context context, String name){
        try{
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(name);
        }catch (Exception e){
            e.toString();
        }
        return "";
    }
}
