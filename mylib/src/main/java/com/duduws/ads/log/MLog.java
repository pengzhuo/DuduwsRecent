package com.duduws.ads.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志类
 * Created by Pengz on 16/7/20.
 */
public class MLog {
    private static final String TAG = "MLog";
    private static boolean isLogEnable = false;

    public static void setLogEnable(boolean flag){
        isLogEnable = flag;
    }

    public static void i(String tag, String msg){
        if (isLogEnable){
            if (TextUtils.isEmpty(tag)){
                Log.i(TAG, "#### " + msg);
            }else{
                Log.i(tag, "#### " + msg);
            }
        }
    }

    public static void i(String msg){
        i(TAG, msg);
    }

    public static void d(String tag, String msg){
        if (isLogEnable){
            if (TextUtils.isEmpty(tag)){
                Log.d(TAG, "#### " + msg);
            }else{
                Log.d(tag, "#### " + msg);
            }
        }
    }

    public static void d(String msg){
        d(TAG, msg);
    }

    public static void w(String tag, String msg){
        if (isLogEnable){
            if (TextUtils.isEmpty(tag)){
                Log.w(TAG, "#### " + msg);
            }else{
                Log.w(tag, "#### " + msg);
            }
        }
    }

    public static void w(String msg){
        w(TAG, msg);
    }

    public static void e(String tag, String msg){
        if (isLogEnable){
            if (TextUtils.isEmpty(tag)){
                Log.e(TAG, "#### " + msg);
            }else{
                Log.e(tag, "#### " + msg);
            }
        }
    }

    public static void e(String msg){
        e(TAG, msg);
    }
}
