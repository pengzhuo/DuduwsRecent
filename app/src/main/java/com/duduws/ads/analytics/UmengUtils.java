package com.duduws.ads.analytics;

import android.content.Context;
import android.text.TextUtils;

import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.utils.TimeUtils;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 友盟打点统计
 * Created by Pengz on 16/7/20.
 */
public class UmengUtils {
    public static void init(Context context){
        AnalyticsConfig.setAppkey(ConstDefine.APP_KEY_UMENG);
        String ver = ConfigDefine.APP_CHANNEL_ID;
        AnalyticsConfig.setChannel(ver);
        MobclickAgent.updateOnlineConfig(context);
        AnalyticsConfig.enableEncrypt(true);
    }

    public static void onEvent(Context context, String eventId, Map<String, String> map) {
        if (context == null || TextUtils.isEmpty(eventId)) {
            return;
        }
        try {
            HashMap<String, String> params = getPubParams(context);
            if( params != null  ){
                if( map != null ){
                    params.putAll(map);
                }
            } else if (map != null ){
                params = (HashMap<String, String>) map ;
            }
            if (params != null) {
                MobclickAgent.onEvent(context, eventId, params);
            } else {
                MobclickAgent.onEvent(context, eventId);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 公共参数:
     * Time - 格式改为：MM-DD
     * @return
     */
    private static HashMap<String, String>  getPubParams(Context context){
        HashMap<String, String> pubParamMap = new HashMap<String, String>();
        String time = TimeUtils.getFormattedTime(System.currentTimeMillis());
        pubParamMap.put("TIME", time);
        return pubParamMap;
    }
}
