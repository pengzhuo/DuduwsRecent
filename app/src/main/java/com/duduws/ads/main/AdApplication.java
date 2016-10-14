package com.duduws.ads.main;

import android.app.Application;
import android.text.TextUtils;

import com.duduws.ads.common.ConfigDefine;
import com.duduws.ads.common.ConstDefine;
import com.duduws.ads.log.MLog;
import com.duduws.ads.utils.DspHelper;
import com.duduws.ads.utils.FuncUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Pengz on 16/7/20.
 */
public class AdApplication extends Application {
    private static final String TAG = "AdApplication";

    private static String CM_APP_ID = "";

    private ArrayList<Integer> lockList = new ArrayList<>();
    private ArrayList<Integer> netList = new ArrayList<>();
    private ArrayList<Integer> appEnterList = new ArrayList<>();
    private ArrayList<Integer> appExitList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        //设置打印日志
        MLog.setLogEnable(true);

        //初始化信息
        initConfigInfo();
    }

    public void initConfigInfo(){
        ConfigDefine.APP_KEY_UMENG      = FuncUtils.getManifestApplicationMetaData(this, "APP_KEY_UMENG");
        ConfigDefine.APP_VERSION 		= FuncUtils.getManifestApplicationMetaData(this, "APP_VERSION");
        ConfigDefine.APP_CHANNEL_ID		= FuncUtils.getManifestApplicationMetaData(this, "APP_CHANNEL_ID");
        ConfigDefine.APP_COOPERATION_ID	= FuncUtils.getManifestApplicationMetaData(this, "APP_COOPERATION_ID");
        ConfigDefine.APP_PRODUCT_ID		= FuncUtils.getManifestApplicationMetaData(this, "APP_PRODUCT_ID");
        ConfigDefine.APP_PROTOCOL		= FuncUtils.getManifestApplicationMetaData(this, "APP_PROTOCOL");

        //设置渠道Site
        String fb_site = FuncUtils.getManifestApplicationMetaData(this, "SITE_FACEBOOK");
        initSiteInfo(ConstDefine.DSP_CHANNEL_FACEBOOK, fb_site);
        String admob_site = FuncUtils.getManifestApplicationMetaData(this, "SITE_ADMOB");
        initSiteInfo(ConstDefine.DSP_CHANNEL_ADMOB, admob_site);
        String cm_site = FuncUtils.getManifestApplicationMetaData(this, "SITE_CM");
        initSiteInfo(ConstDefine.DSP_CHANNEL_CM, cm_site);
        DspHelper.DSP_MAP.clear();
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_UNLOCK, lockList);
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_NETWORK, netList);
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_APP_ENTER, appEnterList);
        DspHelper.DSP_MAP.put(ConstDefine.TRIGGER_TYPE_APP_EXIT, appExitList);

        //设置默认值
        //全局DSP时间间隔
        String dspIntervalGloabl = FuncUtils.getManifestApplicationMetaData(this, "GLOABL_INTERVAL");
        dspIntervalGloabl = dspIntervalGloabl.substring(3, dspIntervalGloabl.length());
        DspHelper.setDspSpotIntervalTime(this, ConstDefine.DSP_GLOABL, Integer.parseInt(dspIntervalGloabl)*1000L);
        //单个SITE时间间隔
        String siteInterval = FuncUtils.getManifestApplicationMetaData(this, "SITE_INTERVAL");
        siteInterval = siteInterval.substring(3, siteInterval.length());
        DspHelper.setDspSpotIntervalTime(this, ConstDefine.DSP_CHANNEL_FACEBOOK, Integer.parseInt(siteInterval)*1000L);
        DspHelper.setDspSpotIntervalTime(this, ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE, Integer.parseInt(siteInterval)*1000L);
        DspHelper.setDspSpotIntervalTime(this, ConstDefine.DSP_CHANNEL_ADMOB, Integer.parseInt(siteInterval)*1000L);
        DspHelper.setDspSpotIntervalTime(this, ConstDefine.DSP_CHANNEL_CM, Integer.parseInt(siteInterval)*1000L);
        //网络连接时间
        String networkTime = FuncUtils.getManifestApplicationMetaData(this, "NETWORK_TIME");
        networkTime = networkTime.substring(3, networkTime.length());
        DspHelper.setNetConTime(this, Integer.parseInt(networkTime));

        MLog.e(TAG, "APP_KEY_UMENG = " + ConfigDefine.APP_KEY_UMENG);
        MLog.e(TAG, "APP_VERSION = " + ConfigDefine.APP_VERSION);
        MLog.e(TAG, "APP_CHANNEL_ID = " + ConfigDefine.APP_CHANNEL_ID);
        MLog.e(TAG, "APP_COOPERATION_ID = " + ConfigDefine.APP_COOPERATION_ID);
        MLog.e(TAG, "APP_PRODUCT_ID = " + ConfigDefine.APP_PRODUCT_ID);
        MLog.e(TAG, "APP_PROTOCOL = " + ConfigDefine.APP_PROTOCOL);
        MLog.e(TAG, "GLOABL_INTERVAL = " + dspIntervalGloabl);
        MLog.e(TAG, "SITE_INTERVAL = " + siteInterval);
        MLog.e(TAG, "NETWORK_TIME = " + networkTime);

        Iterator iterator = DspHelper.DSP_MAP.entrySet().iterator();
        while (iterator.hasNext()){
            MLog.e(TAG, "----------------------------------------");
            Map.Entry entry = (Map.Entry)iterator.next();
            int key = (Integer)entry.getKey();
            ArrayList<Integer> val = (ArrayList<Integer>) entry.getValue();
            MLog.e(TAG, "key --> " + key + "  size: " + val.size());
            for (int i=0; i<val.size(); i++){
                MLog.e(TAG, "" + val.get(i));
            }
            MLog.e(TAG, "----------------------------------------");
        }
    }

    private void initSiteInfo(int type, String json){
        try {
            int diff = ConstDefine.DSP_CHANNEL_FACEBOOK_NATIVE - ConstDefine.DSP_CHANNEL_FACEBOOK;
            int diff_ex = ConstDefine.DSP_CHANNEL_FACEBOOK_VIDEO - ConstDefine.DSP_CHANNEL_FACEBOOK;

            JSONObject jsonObject = new JSONObject(json);
            if (!jsonObject.isNull("sdk")){
                JSONObject sdk = jsonObject.optJSONObject("sdk");
                initSubSite(type, sdk);
            }
            if (!jsonObject.isNull("native")){
                JSONObject nativeObj = jsonObject.optJSONObject("native");
                initSubSite(type+diff, nativeObj);
            }
            if (!jsonObject.isNull("video")){
                JSONObject videoObj = jsonObject.optJSONObject("video");
                initSubSite(type+diff_ex, videoObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSubSite(int channel, JSONObject jsonObject){
        //解锁
        if (!jsonObject.isNull("unlock")){
            if (!TextUtils.isEmpty(jsonObject.optString("unlock"))){
                lockList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_UNLOCK);
            DspHelper.setDspSite(this, offset, jsonObject.optString("unlock"));
            if (channel == ConstDefine.DSP_CHANNEL_CM ||
                    channel == ConstDefine.DSP_CHANNEL_CM_NATIVE ||
                    channel == ConstDefine.DSP_CHANNEL_CM_VIDEO){
                String cmId = jsonObject.optString("unlock");
                if (!TextUtils.isEmpty(cmId)){
                    CM_APP_ID = cmId.substring(0, 4);
                }
            }
        }
        //开网
        if (!jsonObject.isNull("net")){
            if (!TextUtils.isEmpty(jsonObject.optString("net"))){
                netList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_NETWORK);
            DspHelper.setDspSite(this, offset, jsonObject.optString("net"));
        }
        //APP进入
        if (!jsonObject.isNull("enter")){
            if (!TextUtils.isEmpty(jsonObject.optString("enter"))){
                appEnterList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_APP_ENTER);
            DspHelper.setDspSite(this, offset, jsonObject.optString("enter"));
        }
        //APP退出
        if (!jsonObject.isNull("exit")){
            if (!TextUtils.isEmpty(jsonObject.optString("exit"))){
                appExitList.add(channel);
            }
            int offset = channel + DspHelper.getTriggerOffSet(ConstDefine.TRIGGER_TYPE_APP_EXIT);
            DspHelper.setDspSite(this, offset, jsonObject.optString("exit"));
        }
    }
}
