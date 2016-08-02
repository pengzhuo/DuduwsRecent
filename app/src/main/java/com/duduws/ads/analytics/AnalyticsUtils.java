package com.duduws.ads.analytics;

import android.content.Context;

import com.duduws.ads.common.ConfigDefine;

import java.util.HashMap;
import java.util.Map;

/**
 * 打点统计类
 * Created by Pengz on 16/7/20.
 */
public class AnalyticsUtils {
    private static final String TAG = "AnalyticsUtils";
    private static AnalyticsUtils instance;
    private static Context mContext;

    private static final String EVENT_ID_FORMAT_EX = "%d_%d_%d_%d";

    private AnalyticsUtils(Context context){
        mContext = context;
        UmengUtils.init(context);
    }

    public static AnalyticsUtils getInstance(Context context){
        if (instance == null){
            instance = new AnalyticsUtils(context);
        }
        return instance;
    }

    /**
     * 打点
     * @param context
     * @param adType  广告主  1 facebook  2 admob  3 cm
     * @param triggerId  触发类型  1 解锁屏  2 开网络  3 App应用进入  4 App应用退出
     * @param posType  广告类型  1 弹窗  2 视屏  3 banner  4 原生
     * @param resultType  展示点击事件  1 请求广告  2 请求广告成功  3 请求广告失败  4 展示广告  5 点击广告  6 关闭广告
     */
    public static void onEvent(Context context, int adType, int triggerId, int posType, int resultType){
        String eventId = String.format(EVENT_ID_FORMAT_EX, adType, triggerId, posType, resultType);
        Map<String, String> map = new HashMap<String, String>();
        map.put("cid", ConfigDefine.APP_CHANNEL_ID);
        map.put("pid", ConfigDefine.APP_PRODUCT_ID);
        map.put("version", ConfigDefine.APP_VERSION);
        map.put("oid", ConfigDefine.APP_COOPERATION_ID);
        UmengUtils.onEvent(context, eventId, map);
    }
}
