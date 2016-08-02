package com.duduws.ads.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.duduws.ads.log.MLog;

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
        } else if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //网络发生变化
        } else if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            //设备重启完成
        }
    }
}
